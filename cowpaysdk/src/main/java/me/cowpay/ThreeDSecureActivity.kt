package me.cowpay

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.MailTo
import android.net.Uri
import android.os.Bundle
import android.webkit.*
import androidx.core.content.ContextCompat
import me.cowpay.databinding.ActivityThreeDSecureBinding
import me.cowpay.network_connection.JsonParser
import me.cowpay.util.CowpayConstantKeys


class ThreeDSecureActivity : CowPayBaseActivity(
    R.string.three__d_secure, true, true, false,
    false, false, true, true
) {
    lateinit var binding: ActivityThreeDSecureBinding
    override fun doOnCreate(arg0: Bundle?) {
        binding = putContentView(R.layout.activity_three_d_secure) as ActivityThreeDSecureBinding
        initializeViews()
        setListener()
    }

    override fun initializeViews() {
        getIntentData()
        prepareWebView()
    }

    var threeDUrl = ""
    var isOperationSuccess = false
    var payment_gateway_reference_id = ""

    fun getIntentData() {
        threeDUrl = intent.extras!!.getString("threeDUrl")!!
    }

    override fun onBackPressed() {
        var intent = Intent()
        if (!isOperationSuccess) {
            intent.putExtra(CowpayConstantKeys.ResponseCode, CowpayConstantKeys.ErrorCode)
            intent.putExtra(
                CowpayConstantKeys.ResponseMessage,
                getString(R.string.operation_failed)
            )
            intent.putExtra(
                CowpayConstantKeys.PaymentGatewayReferenceId,
                payment_gateway_reference_id
            )
        } else {
            intent.putExtra(CowpayConstantKeys.ResponseCode, CowpayConstantKeys.SuccessCode)
            intent.putExtra(
                CowpayConstantKeys.ResponseMessage,
                getString(R.string.operation_done_successfully)
            )
            intent.putExtra(
                CowpayConstantKeys.PaymentGatewayReferenceId,
                payment_gateway_reference_id
            )
        }
        setResult(RESULT_OK, intent)
        finish_activity()
    }

    fun webViewUrlChanges(view: WebView, uri: Uri) {
        val scheme = uri.scheme
        if (scheme!!.startsWith("mailto:")) {
            val mt = MailTo.parse(scheme)
            val i = newEmailIntent(
                mt.to,
                mt.subject, mt.body, mt.cc
            )
            startActivity(i)
            view.reload()
        } else {
            view.loadUrl(scheme)
        }
    }

    override fun setListener() {
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun prepareWebView() {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(binding.webview, true)
        } else {
            CookieManager.getInstance().setAcceptCookie(true)
        }

        binding.webview.isHorizontalScrollBarEnabled = false
        binding.webview.clearCache(true)
        binding.webview.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

        binding.webview.settings.domStorageEnabled = true
        binding.webview.settings.javaScriptEnabled = true

        binding.webview.settings.loadWithOverviewMode = true
        binding.webview.settings.useWideViewPort = true

        binding.webview.settings.displayZoomControls = false
        binding.webview.settings.builtInZoomControls = true
        binding.webview.settings.setSupportZoom(true)

        binding.webview.settings.loadsImagesAutomatically = true
        binding.webview.settings.blockNetworkImage = false
        binding.webview.settings.setGeolocationEnabled(true)
        binding.webview.settings.setNeedInitialFocus(true)
        binding.webview.settings.defaultTextEncodingName = "utf-8"

        binding.webview.settings.allowFileAccess = true
        binding.webview.settings.allowContentAccess = true
        binding.webview.settings.javaScriptCanOpenWindowsAutomatically = true
        binding.webview.loadUrl(threeDUrl)

        binding.webview.webViewClient = buildWebViewClient()

        binding.webview.addJavascriptInterface(JSBridge(object : OnPaymentResult {
            override fun onPaymentResult(response: String) {
                try {
                    var parentResponseModel = JsonParser().getParentResponseModel(response)
                    if (parentResponseModel != null) {
                        if (parentResponseModel.payment_status.compareTo("PAID") == 0) {
                            payment_gateway_reference_id =
                                parentResponseModel.payment_gateway_reference_id
                            isOperationSuccess = true
                            onBackPressed()
                        } else {
                            isOperationSuccess = false
                            onBackPressed()
                        }
                    } else {
                        isOperationSuccess = false
                        onBackPressed()
                    }
                } catch (e: Exception) {
                    isOperationSuccess = false
                    onBackPressed()
                }
            }

        }), "JSBridge")
    }

    interface OnPaymentResult {
        fun onPaymentResult(response: String)
    }

    class JSBridge(var onPaymentResult: OnPaymentResult) {
        @JavascriptInterface
        fun showMessageInNative(response: String) {
            //Received message from webview in native, process data
            onPaymentResult.onPaymentResult(response)
        }
    }

    fun buildWebViewClient(): WebViewClient {
        return object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                webViewUrlChanges(view, Uri.parse(url))
                return true
            }

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                showProgressDialog()
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                dismissProgressDialog()
            }
        }
    }

    fun newEmailIntent(
        address: String,
        subject: String, body: String, cc: String
    ): Intent {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
        intent.putExtra(Intent.EXTRA_TEXT, body)
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_CC, cc)
        intent.type = "message/rfc822"
        return intent
    }

}