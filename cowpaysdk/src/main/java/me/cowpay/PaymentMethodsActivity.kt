package me.cowpay

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import me.cowpay.databinding.ActivityPaymentMethodsBinding
import me.cowpay.observer.OnAskUserAction
import me.cowpay.util.CowpayConstantKeys
import me.cowpay.view.sub.PopupDialogAskUserAction

class PaymentMethodsActivity : CowPayBaseActivity(
    R.string.no_text, true, true, true, false,
    false, true, true
) {
    lateinit var binding: ActivityPaymentMethodsBinding
    override fun doOnCreate(arg0: Bundle?) {
        binding = putContentView(R.layout.activity_payment_methods) as ActivityPaymentMethodsBinding
        initializeViews()
        setListener()
    }

    override fun initializeViews() {
        if (checIntentData()) {
            var PaymentMethod =
                intent.extras!!.getStringArrayList(CowpayConstantKeys.PaymentMethod)
            if (PaymentMethod!!.size == 1) {
                if (PaymentMethod!![0].compareTo(CowpayConstantKeys.CreditCardMethod) == 0) {
                    binding.layoutFawryPaymentMethodsActivity.visibility = View.GONE
                    openCreditCardScreen()
                } else if (PaymentMethod[0].compareTo(CowpayConstantKeys.FawryMethod) == 0) {
                    binding.layoutCreditCardPaymentMethodsActivity.visibility = View.GONE
                    binding.line1.visibility = View.GONE
                    openFawryScreen()
                }
            } else if (PaymentMethod!!.size > 1) {
                if (PaymentMethod!!.contains(CowpayConstantKeys.CreditCardMethod)) {
                    binding.layoutCreditCardPaymentMethodsActivity.visibility = View.VISIBLE
                    binding.line1.visibility = View.VISIBLE
                } else {
                    binding.layoutCreditCardPaymentMethodsActivity.visibility = View.GONE
                    binding.line1.visibility = View.GONE
                }
                if (PaymentMethod.contains(CowpayConstantKeys.FawryMethod)) {
                    binding.layoutFawryPaymentMethodsActivity.visibility = View.VISIBLE
                } else {
                    binding.layoutFawryPaymentMethodsActivity.visibility = View.GONE
                }
            }
        }
    }

    override fun setListener() {
        binding.layoutCreditCardPaymentMethodsActivity.setOnClickListener {
            openCreditCardScreen()
        }
        binding.layoutFawryPaymentMethodsActivity.setOnClickListener {
            openFawryScreen()
        }
    }

    fun openCreditCardScreen() {
        var nextIntent = Intent(this@PaymentMethodsActivity, CreditCardActivity::class.java)
        nextIntent = prepareIntent(nextIntent)
        startActivityForResult(nextIntent, CowpayConstantKeys.RequestCodeCreditCardActivity)
        overridePendingTransition(R.anim.slide_from_right_to_left, R.anim.slide_in_left)
    }

    fun openFawryScreen() {
        var nextIntent = Intent(this@PaymentMethodsActivity, FawryActivity::class.java)
        nextIntent = prepareIntent(nextIntent)
        startActivityForResult(nextIntent, CowpayConstantKeys.RequestCodeFawryActivity)
        overridePendingTransition(R.anim.slide_from_right_to_left, R.anim.slide_in_left)
    }

    fun prepareIntent(nextIntent: Intent): Intent {
        //check user credit card pay or save
        if (intent.hasExtra(CowpayConstantKeys.CreditCardMethodType)) {
            nextIntent.putExtra(
                CowpayConstantKeys.CreditCardMethodType,
                intent.extras!!.getString(CowpayConstantKeys.CreditCardMethodType)
            )
        } else {
            nextIntent.putExtra(
                CowpayConstantKeys.CreditCardMethodType, CowpayConstantKeys.CreditCardMethodPay
            )
        }
        //authorization Token
        nextIntent.putExtra(
            CowpayConstantKeys.AuthorizationToken,
            intent.extras!!.getString(CowpayConstantKeys.AuthorizationToken)
        )
        //merchant data
        nextIntent.putExtra(
            CowpayConstantKeys.MerchantCode,
            intent.extras!!.getString(CowpayConstantKeys.MerchantCode)
        )
        nextIntent.putExtra(
            CowpayConstantKeys.MerchantHashKey,
            intent.extras!!.getString(CowpayConstantKeys.MerchantHashKey)
        )
        //order id
        nextIntent.putExtra(
            CowpayConstantKeys.MerchantReferenceId,
            intent.extras!!.getString(CowpayConstantKeys.MerchantReferenceId)
        )
        //order price
        nextIntent.putExtra(
            CowpayConstantKeys.Amount,
            intent.extras!!.getString(CowpayConstantKeys.Amount)
        )
        //user id
        nextIntent.putExtra(
            CowpayConstantKeys.CustomerMerchantProfileId,
            intent.extras!!.getString(CowpayConstantKeys.CustomerMerchantProfileId)
        )
        //user name
        if (intent.hasExtra(CowpayConstantKeys.CustomerName))
            nextIntent.putExtra(
                CowpayConstantKeys.CustomerName,
                intent.extras!!.getString(CowpayConstantKeys.CustomerName)
            )
        else
            nextIntent.putExtra(CowpayConstantKeys.CustomerName, "John Smith")
        //user mobile
        if (intent.hasExtra(CowpayConstantKeys.CustomerMobile))
            nextIntent.putExtra(
                CowpayConstantKeys.CustomerMobile,
                intent.extras!!.getString(CowpayConstantKeys.CustomerMobile)
            )
        else
            nextIntent.putExtra(CowpayConstantKeys.CustomerMobile, "+201096545211")
        //user email
        if (intent.hasExtra(CowpayConstantKeys.CustomerEmail))
            nextIntent.putExtra(
                CowpayConstantKeys.CustomerEmail,
                intent.extras!!.getString(CowpayConstantKeys.CustomerEmail)
            )
        else
            nextIntent.putExtra(CowpayConstantKeys.CustomerEmail, "customer@customer.com")

        //Description
        if (intent.hasExtra(CowpayConstantKeys.Description))
            nextIntent.putExtra(
                CowpayConstantKeys.Description,
                intent.extras!!.getString(CowpayConstantKeys.Description)
            )
        else
            nextIntent.putExtra(CowpayConstantKeys.Description, "example description")

        //Payment environment
        if (intent.hasExtra(CowpayConstantKeys.PaymentEnvironment))
            nextIntent.putExtra(
                CowpayConstantKeys.PaymentEnvironment,
                intent.extras!!.getString(CowpayConstantKeys.PaymentEnvironment)
            )
        else
            nextIntent.putExtra(
                CowpayConstantKeys.PaymentEnvironment,
                CowpayConstantKeys.Production
            )
        //set locale, english is default
        if (intent.hasExtra(CowpayConstantKeys.Language))
            nextIntent.putExtra(
                CowpayConstantKeys.Language,
                intent.extras!!.getString(CowpayConstantKeys.Language)
            )
        else
            nextIntent.putExtra(
                CowpayConstantKeys.Language,
                CowpayConstantKeys.ENGLISH
            )
        return nextIntent
    }

    fun checIntentData(): Boolean {
        if (intent != null && intent.extras != null) {
            if (!intent.hasExtra(CowpayConstantKeys.MerchantCode)) {
                showMessagePopup(
                    getString(R.string.missing_merchant_code_msg), CowpayConstantKeys.ErrorCode
                )
                return false
            } else if (!intent.hasExtra(CowpayConstantKeys.AuthorizationToken)) {
                showMessagePopup(
                    getString(R.string.missing_authorization_token_key_msg),
                    CowpayConstantKeys.ErrorCode
                )
                return false
            } else if (!intent.hasExtra(CowpayConstantKeys.MerchantHashKey)) {
                showMessagePopup(
                    getString(R.string.missing_merchant_hash_key_msg),
                    CowpayConstantKeys.ErrorCode
                )
                return false
            } else if (!intent.hasExtra(CowpayConstantKeys.MerchantReferenceId)) {
                showMessagePopup(
                    getString(R.string.missing_merchant_reference_id_msg),
                    CowpayConstantKeys.ErrorCode
                )
                return false
            } else if (!intent.hasExtra(CowpayConstantKeys.CustomerMerchantProfileId)) {
                showMessagePopup(
                    getString(R.string.missing_customer_merchant_profile_id_msg),
                    CowpayConstantKeys.ErrorCode
                )
                return false
            } else if (!intent.hasExtra(CowpayConstantKeys.Amount)) {
                showMessagePopup(
                    getString(R.string.missing_amount_msg), CowpayConstantKeys.ErrorCode
                )
                return false
            } else if (!intent.hasExtra(CowpayConstantKeys.PaymentMethod)) {
                showMessagePopup(
                    getString(R.string.missing_payment_method_msg), CowpayConstantKeys.ErrorCode
                )
                return false
            }
            return true
        }

        showMessagePopup(
            getString(R.string.missing_merchant_code_msg), CowpayConstantKeys.ErrorCode
        )
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CowpayConstantKeys.RequestCodeCreditCardActivity && data != null && resultCode == Activity.RESULT_OK) {
            var responseCode = data.extras!!.getInt(CowpayConstantKeys.ResponseCode, 0)
            var responseMSG = data.extras!!.getString(CowpayConstantKeys.ResponseMessage)
            var paymentMethod = data.extras!!.getString(CowpayConstantKeys.PaymentMethod)
            var paymentGatewayReferenceId =
                data.extras!!.getString(CowpayConstantKeys.PaymentGatewayReferenceId)
            finishWithMessage(
                responseMSG!!,
                responseCode,
                paymentGatewayReferenceId!!,
                paymentMethod!!
            )
        } else if (requestCode == CowpayConstantKeys.RequestCodeFawryActivity && data != null && resultCode == Activity.RESULT_OK) {
            var responseCode = data.extras!!.getInt(CowpayConstantKeys.ResponseCode, 0)
            var responseMSG = data.extras!!.getString(CowpayConstantKeys.ResponseMessage)
            var paymentMethod = data.extras!!.getString(CowpayConstantKeys.PaymentMethod)
            var paymentGatewayReferenceId =
                data.extras!!.getString(CowpayConstantKeys.PaymentGatewayReferenceId)
            finishWithMessage(
                responseMSG!!,
                responseCode,
                paymentGatewayReferenceId!!,
                paymentMethod!!
            )
        }
    }

    fun showMessagePopup(msg: String, responseCode: Int) {
        var popupDialogAskUserAction = PopupDialogAskUserAction()
        popupDialogAskUserAction.setOnAskUserActionObserver(object : OnAskUserAction {
            override fun onPositiveAction() {
                popupDialogAskUserAction.dismiss()
                finishWithMessage(msg, responseCode, "", "")
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
        popupDialogAskUserAction.isCancelable = false
        popupDialogAskUserAction.show(supportFragmentManager, "PopupDialogAskUserAction")
    }

    fun finishWithMessage(
        msg: String,
        responseCode: Int,
        payment_gateway_reference_id: String,
        paymentMethod: String
    ) {
        var intent = Intent()
        intent.putExtra(CowpayConstantKeys.ResponseCode, responseCode)
        intent.putExtra(CowpayConstantKeys.ResponseMessage, msg)
        intent.putExtra(CowpayConstantKeys.PaymentMethod, paymentMethod)
        intent.putExtra(
            CowpayConstantKeys.PaymentGatewayReferenceId,
            payment_gateway_reference_id
        )
        setResult(Activity.RESULT_OK, intent)
        finish_activity()
    }

}