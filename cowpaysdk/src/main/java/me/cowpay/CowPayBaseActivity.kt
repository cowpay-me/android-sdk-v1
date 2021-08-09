package me.cowpay

import android.Manifest
import android.annotation.TargetApi
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.security.ProviderInstaller
import me.cowpay.databinding.ActivityBaseCowpayBinding
import me.cowpay.observer.OnAskUserAction
import me.cowpay.util.CowpayConstantKeys
import me.cowpay.util.LocaleHelper
import me.cowpay.view.sub.PopupDialogAskUserAction
import java.util.*
import kotlin.properties.Delegates

abstract class CowPayBaseActivity : AppCompatActivity {
    var drawHeader: Boolean by Delegates.notNull<Boolean>()

    internal var showBack: Boolean by Delegates.notNull<Boolean>()
    internal var isCloseIcon: Boolean by Delegates.notNull<Boolean>()
    internal var showMenu: Boolean by Delegates.notNull<Boolean>()
    internal var showAny: Boolean by Delegates.notNull<Boolean>()
    internal var showCall: Boolean by Delegates.notNull<Boolean>()
    internal var appBarWhite: Boolean by Delegates.notNull<Boolean>()
    // to allow sliding menu or not

    private var activityTitleId: Int = 0

    constructor(
        activityTitleId: Int,
        drawHeader: Boolean,
        showBack: Boolean,
        isCloseIcon: Boolean,
        showMenu: Boolean,
        showAny: Boolean,
        showCall: Boolean,
        appBarWhite: Boolean
    ) : super() {
        this.drawHeader = drawHeader
        this.showBack = showBack
        this.isCloseIcon = isCloseIcon
        this.showMenu = showMenu
        this.showAny = showAny
        this.showCall = showCall
        this.appBarWhite = appBarWhite
        this.activityTitleId = activityTitleId
    }


    protected abstract fun doOnCreate(arg0: Bundle?)
    abstract fun initializeViews()

    fun setTitleGravity(gravity: Int) {
        baseBinding.tvTitleCustomActionBar.gravity = gravity
    }

    abstract fun setListener()

    var imm: InputMethodManager by Delegates.notNull<InputMethodManager>()

    fun updateLocale(){
        var locale = "en"
        if (intent.hasExtra(CowpayConstantKeys.Language))
            locale = intent.getStringExtra(CowpayConstantKeys.Language)!!
        if (locale.contains("ar")) {
            locale = "ar"
            //RTL
            forceRTLIfSupported()
        } else {
            locale = "en"
            //LTR
            forceLTRIfSupported()
        }
        //Update the locale here before loading the layout to get localized strings.
        LocaleHelper.updateLocale(this, locale)
    }

    lateinit var baseBinding: ActivityBaseCowpayBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateAndroidSecurityProvider()
        updateLocale()
        baseBinding = DataBindingUtil.setContentView(this, R.layout.activity_base_cowpay)
        //set default color for appbar
        setTranslucentAppBar()

        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        //set actionbar
        setDrawHeader(
            drawHeader,
            getString(activityTitleId),
            showBack,
            isCloseIcon,
            showAny,
            showMenu,
            showCall,
            appBarWhite
        )

        doOnCreate(savedInstanceState)
        setListener()
        backBtnAction()
        callBtnAction()
    }

    private fun updateAndroidSecurityProvider() {
        try {
            //enable provider in some devices that disabled by default to avoid ssl error in connection
            ProviderInstaller.installIfNeeded(this)
        } catch (e: GooglePlayServicesRepairableException) {
        } catch (e: GooglePlayServicesNotAvailableException) {
        }
    }

    fun putContentView(activityLayout: Int): ViewDataBinding {
        return DataBindingUtil.inflate(
            layoutInflater,
            activityLayout,
            baseBinding.baseFragment,
            true
        )
    }

    fun setCallIconVisibility(isVisible: Boolean) {
        //baseBinding.ivAnyActionActionBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    fun setActionBarVisibilty(isVisible: Boolean) {
        baseBinding.layoutContainerActionBar.visibility =
            if (isVisible) View.VISIBLE else View.GONE
    }

    internal fun backBtnAction() {
        baseBinding.ivbackActionBar.setOnClickListener {
            onBackPressed()
        }
    }

    internal fun callBtnAction() {
    }

    fun convertArabicNumbersToEnglish(value: String): String {
        return value.replace("١".toRegex(), "1").replace("٢".toRegex(), "2")
            .replace("٣".toRegex(), "3")
            .replace("٤".toRegex(), "4").replace("٥".toRegex(), "5").replace("٦".toRegex(), "6")
            .replace("٧".toRegex(), "7").replace("٨".toRegex(), "8").replace("٩".toRegex(), "9")
            .replace("٠".toRegex(), "0")
    }

    fun setHeaderTitle(title: String) {
        baseBinding.tvTitleCustomActionBar.text = title
    }

    internal fun setTranslucentAppBar() {
        val fullScreen = window.attributes.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN != 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (fullScreen) {
                baseBinding.layoutContainerActionBar.setVisibility(View.GONE)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
                }
            } else {
                setAppBarGradient()
            }
        } else {
            if (!fullScreen) {
                setAppBarGradient()
            }
        }
    }

    fun setDrawHeader(
        isShowHeader: Boolean,
        title: String,
        isShowBackIcon: Boolean,
        isCloseIcon: Boolean,
        showAny: Boolean,
        showMenu: Boolean,
        showcallBtn: Boolean,
        appBarWhite: Boolean
    ) {
        setActionBarVisibilty(isShowHeader)
        setBackIconVisibility(isShowBackIcon, isCloseIcon, showMenu, appBarWhite)
        setCallIconVisibility(showcallBtn)
        setHeaderTitle(title)
        if (appBarWhite) {
            setAppBarlightAndStatusBarDark(R.color.white)
        } else {
            setAppBarGradient()
            baseBinding.layoutContainerActionBar.setBackgroundResource(R.color.colorPrimary)
            baseBinding.tvTitleCustomActionBar.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
        }
    }

    fun setBackIconVisibility(
        isShowBackIcon: Boolean,
        isCloseIcon: Boolean,
        showMenu: Boolean, appBarWhite: Boolean
    ) {


        //set icons
        if (appBarWhite) {
            if (isShowBackIcon) {
                if (isCloseIcon) {
                    baseBinding.ivbackActionBar.setImageResource(R.drawable.close_black_icon)
                } else {
                    baseBinding.ivbackActionBar.setImageResource(R.drawable.back_black_icon)
                }
            } else if (showMenu) {
                baseBinding.ivbackActionBar.setImageResource(R.drawable.close_black_icon)
                //disable slide menu
            } else {
                baseBinding.ivbackActionBar.visibility =
                    if (isShowBackIcon || showMenu) View.VISIBLE else View.INVISIBLE
            }
        } else {
            if (isShowBackIcon) {
                if (isCloseIcon) {
                    baseBinding.ivbackActionBar.setImageResource(R.drawable.close_black_icon)
                } else {
                    baseBinding.ivbackActionBar.setImageResource(R.drawable.back_black_icon)
                }
            } else if (showMenu) {
                baseBinding.ivbackActionBar.setImageResource(R.drawable.close_black_icon)
            } else {
                baseBinding.ivbackActionBar.visibility =
                    if (isShowBackIcon || showMenu) View.VISIBLE else View.INVISIBLE
            }
        }
    }

    fun setAppBarlightAndStatusBarDark(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //make statusbar dark text and icons starting from KITKAT
            baseBinding.layoutContainerBaseActivity.systemUiVisibility =
                baseBinding.layoutContainerBaseActivity.systemUiVisibility or
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, color)
            window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
        }
        baseBinding.layoutContainerActionBar.setBackgroundResource(color)
        baseBinding.tvTitleCustomActionBar.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.outer_space
            )
        )
        window.setBackgroundDrawableResource(R.color.black)
    }

    fun setAppBarGradient() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //make statusbar dark text and icons starting from KITKAT
            baseBinding.layoutContainerBaseActivity.systemUiVisibility =
                baseBinding.layoutContainerBaseActivity.systemUiVisibility and
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()

            window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
            window.statusBarColor = ContextCompat.getColor(this, R.color.transparent)
        }
        //set gradient color
        baseBinding.layoutContainerActionBar.setBackgroundResource(R.color.colorPrimary)
        baseBinding.tvTitleCustomActionBar.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.white
            )
        )
        window.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.colorPrimary))
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public fun forceRTLIfSupported() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public fun forceLTRIfSupported() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR
        }
    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        super.applyOverrideConfiguration(
            LocaleHelper.applyOverrideConfiguration(
                baseContext,
                overrideConfiguration
            )
        )
    }

    override fun attachBaseContext(newBase: Context) {
        var context = newBase
        var locale = CowpayConstantKeys.LanguageCode
        newBase.let {
            context = LocaleHelper.updateLocale(it, locale)
        }
        super.attachBaseContext(context)
    }

    /*fun getLocal(): String {
        return resources.configuration.locale.toString()
    }*/

    /*fun ChangeLocale(local: String) {
        val locale = Locale(local)
        Locale.setDefault(locale)
        val config = baseContext.resources.configuration
        config.setLocale(locale)
        baseContext.createConfigurationContext(config)
        baseContext.resources.updateConfiguration(
            config, baseContext.resources
                .displayMetrics
        )
    }*/

    fun getOSVersion(): String {
        return (Build.VERSION_CODES::class.java.fields[android.os.Build.VERSION.SDK_INT].name)
    }

    internal val callPermissionRequest = 4

    var number = ""
    fun CallMobile(Number: String?) {
        try {
            number = Number!!
            if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), callPermissionRequest)
                return
            }
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.data = Uri.parse("tel:$Number")
            callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                callIntent.setPackage("com.android.server.telecom")
            } else {
                callIntent.setPackage("com.android.phone")
            }
            startActivity(callIntent)
        } catch (e: Exception) {
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.data = Uri.parse("tel:$Number")
            callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(callIntent)
        }

    }

    fun ShareApplication() {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        val palystoreApplink = "http://play.google.com/store/apps/details?id=".plus(packageName)
        val subject = resources.getString(R.string.app_name)
        sharingIntent.putExtra(Intent.EXTRA_TEXT, subject + "\n" + palystoreApplink)
        startActivity(Intent.createChooser(sharingIntent, resources.getString(R.string.share_via)))
    }

    fun shareToOtherApps(text: String) {
        // share
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(sharingIntent, "Share via"))
    }

    fun openLink(url: String, view: View) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: Exception) {
            showMessage(view, getString(R.string.error_while_open_link))
        }
    }

    fun copyText(text: String) {
        val clipboard =
            getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData
            .newPlainText("text label", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this@CowPayBaseActivity, getString(R.string.code_copied), Toast.LENGTH_SHORT)
            .show()
    }

    internal fun requestReceiveSMSPermission() {
        if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECEIVE_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.RECEIVE_SMS), 5)
            return
        }
    }

    fun hasPermissions(context: Context?, permissions: Array<String>): Boolean {
        if (context != null && permissions != null) {
            for (p in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        p
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            callPermissionRequest ->
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    CallMobile(number)
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            else -> {
            }
        }
    }

    fun setSwipeRefreshLayoutColor(swipeRefreshLayout: SwipeRefreshLayout) {
        var colorPrimaryDark = 0
        colorPrimaryDark = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        swipeRefreshLayout.setColorSchemeColors(
            colorPrimaryDark,
            colorPrimaryDark,
            colorPrimaryDark
        )
    }

    fun sendSMSTo(Number: String, subject: String) {
        val uri = Uri.parse("smsto:$Number")
        val smsIntent = Intent(Intent.ACTION_SENDTO, uri)
        smsIntent.putExtra("sms_body", subject)
        startActivity(smsIntent)
    }

    open fun finish_activity() {
        finish()
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_from_left_to_right)
    }

    fun replaceCurrentFragment(
        container: Int,
        targetFragment: androidx.fragment.app.Fragment,
        tag: String,
        addToBackStack: Boolean,
        animate: Boolean
    ) {
        val manager = supportFragmentManager
        var fragmentPopped = false
        fragmentPopped = manager.popBackStackImmediate(tag, 0)
        if (!fragmentPopped && manager.findFragmentByTag(tag) == null) {
            val ft = manager.beginTransaction()
            if (animate) ft.setCustomAnimations(
                R.anim.slide_from_right_to_left,
                R.anim.slide_in_left,
                R.anim.slide_out_left,
                R.anim.slide_from_left_to_right
            )
            //                ft.setCustomAnimations(R.anim.push_down_out, R.anim.top_in, R.anim.top_out, R.anim.push_down_in);
            ft.replace(container, targetFragment, tag)
            supportFragmentManager.executePendingTransactions()
            if (addToBackStack) {
                ft.addToBackStack(tag)
            }
            ft.commit()
        }
    }

    fun hideKeyPad(view: View) {
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    internal var progressDialog: Dialog? = null

    fun showProgressDialog() {

        try {
            if (!isFinishing && progressDialog != null && progressDialog!!.isShowing)
                dismissProgressDialog()
            progressDialog = Dialog(this, R.style.MyDialog)
            progressDialog!!.window!!.setBackgroundDrawable(
                ColorDrawable(ContextCompat.getColor(this, R.color.transparent_black))
            )
            progressDialog!!.getWindow()!!.attributes.windowAnimations =
                android.R.anim.cycle_interpolator
            progressDialog!!.show()
            progressDialog!!.setCancelable(true)
            progressDialog!!.setCanceledOnTouchOutside(false)
            progressDialog!!.setContentView(R.layout.custom_progress_dialog)
            var tvMSG_customProgressDialog =
                progressDialog!!.findViewById<TextView>(R.id.tvMSG_customProgressDialog)
            tvMSG_customProgressDialog.setText(getString(R.string.please_wait))
        } catch (e: Exception) {
        }
    }

    fun updateProgressText(msg: String) {
        try {
            if (!isFinishing && progressDialog != null && progressDialog!!.isShowing) {
                val tvMSG_customProgressDialog =
                    progressDialog!!.findViewById<TextView>(R.id.tvMSG_customProgressDialog)
                tvMSG_customProgressDialog.setText(msg)
            }
        } catch (e: Exception) {
        }
    }

    fun dismissProgressDialog() {
        if (!isFinishing && progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
            progressDialog = null
        }
    }

    fun showMessage(view: View, msg: String) {
        //        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).setActionTextColor(getResources().getColor(R.color.white)).show();
        /* val snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG)

         // Changing action button text color
         snackbar.setActionTextColor(Color.WHITE)
         // Changing message text color
         val sbView = snackbar.view
         val textView = sbView.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
         textView.setTextColor(Color.WHITE)

         snackbar.show()*/


        var popupDialogAskUserAction = PopupDialogAskUserAction()
        popupDialogAskUserAction.setOnAskUserActionObserver(object : OnAskUserAction {
            override fun onPositiveAction() {
                popupDialogAskUserAction.dismiss()
            }

            override fun onNegativeAction() {
            }
        })
        var bundle = Bundle()
        bundle.putString("title", getString(R.string.app_name))
        bundle.putString("body", msg)
        bundle.putString("negativeButtonText", getString(R.string.cancel))
        bundle.putString("positiveButtonText", getString(R.string.ok))
        bundle.putBoolean("isShowTitle", false)
        bundle.putBoolean("isShowNegativeButton", false)
        bundle.putBoolean("isShowPositiveButton", true)
        popupDialogAskUserAction.arguments = bundle
        popupDialogAskUserAction.show(supportFragmentManager, "PopupDialogAskUserAction")
    }
}