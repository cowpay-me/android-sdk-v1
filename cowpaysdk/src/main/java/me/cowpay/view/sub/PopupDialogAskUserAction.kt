package me.cowpay.view.sub

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import me.cowpay.CowPayBaseActivity
import me.cowpay.R
import me.cowpay.databinding.PopupDialogSureBinding
import me.cowpay.observer.OnAskUserAction

class PopupDialogAskUserAction : BaseDialogFragment() {

    internal var activityCowPay: CowPayBaseActivity? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CowPayBaseActivity) {
            activityCowPay = context
        }
    }

    lateinit var binding: PopupDialogSureBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (activityCowPay == null) activityCowPay = getActivity() as CowPayBaseActivity?
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.popup_dialog_sure, container, false)
        return binding.root
    }

    lateinit var onAskUserAction: OnAskUserAction

    fun setOnAskUserActionObserver(onAskUserAction: OnAskUserAction) {
        this.onAskUserAction = onAskUserAction
    }

    lateinit internal var dialog: Dialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (activityCowPay == null)
            activityCowPay = getActivity() as CowPayBaseActivity?
        dialog = Dialog(activityCowPay!!)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog = Dialog(activityCowPay!!, R.style.FullWidthDialogTheme)
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
        dialog.window!!.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog.window!!.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL)
        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initializeViews()
        setListener()
    }


    var title = ""
    var body = ""
    var negativeButtonText = ""
    var positiveButtonText = ""
    var isShowTitle = false
    var isShowNegativeButton = false
    var isShowPositiveButton = false

    fun initializeViews() {
        getArgumentsData()

        setData()
    }

    fun getArgumentsData() {
        if (arguments != null) {
            title = arguments!!.getString("title", "")
            body = arguments!!.getString("body", "")
            negativeButtonText = arguments!!.getString("negativeButtonText", "")
            positiveButtonText = arguments!!.getString("positiveButtonText", "")
            isShowTitle = arguments!!.getBoolean("isShowTitle", false)
            isShowNegativeButton = arguments!!.getBoolean("isShowNegativeButton", false)
            isShowPositiveButton = arguments!!.getBoolean("isShowPositiveButton", false)
        }
    }

    fun setData() {
        binding.txtviewDialogSureTitle.text = title
        binding.txtviewDialogSureBody.text = body
        binding.btnDialogSureCancel.text = negativeButtonText
        binding.btnDialogSureOk.text = positiveButtonText

        binding.txtviewDialogSureTitle.visibility = if (isShowTitle) View.VISIBLE else View.GONE
        binding.btnDialogSureCancel.visibility = if (isShowNegativeButton) View.VISIBLE else View.GONE
        binding.btnDialogSureOk.visibility = if (isShowPositiveButton) View.VISIBLE else View.GONE
    }

    fun setListener() {
        binding.btnDialogSureOk.setOnClickListener {
            if (::onAskUserAction.isInitialized)
                onAskUserAction.onPositiveAction()
            dismissAllowingStateLoss()
        }

        binding.btnDialogSureCancel.setOnClickListener {
            if (::onAskUserAction.isInitialized)
                onAskUserAction.onNegativeAction()
            dismissAllowingStateLoss()
        }

    }

}