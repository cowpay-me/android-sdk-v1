package me.cowpay

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import me.cowpay.cardlib.OtherCardTextWatcher
import me.cowpay.databinding.ActivityCreditCardBinding
import me.cowpay.model.ItemModel
import me.cowpay.network_connection.*
import me.cowpay.util.CowpayConstantKeys
import me.cowpay.util.Hasher
import me.cowpay.util.MathUtils
import okhttp3.MultipartBody
import java.util.*
import kotlin.collections.ArrayList


class CreditCardActivity : CowPayBaseActivity(
    R.string.no_text, true, true, false,
    false, false, true, true
) {
    lateinit var binding: ActivityCreditCardBinding
    override fun doOnCreate(arg0: Bundle?) {
        binding = putContentView(R.layout.activity_credit_card) as ActivityCreditCardBinding
        initializeViews()
        setListener()
    }

    override fun initializeViews() {
        binding.layoutErrorCreditCardActivity.visibility = View.GONE
        binding.layoutContainerCreditCardActivity.visibility = View.VISIBLE
        getIntentData()
    }

    var MerchantCode = ""
    var Description = ""
    var AuthorizationToken = ""
    var MerchantHashKey = ""
    var MerchantReferenceId = ""
    var Amount = ""
    var CustomerMerchantProfileId = ""
    var CustomerName = ""
    var CustomerMobile = ""
    var CustomerEmail = ""
    var EnvironmentPayment = ""

    fun getIntentData() {
        EnvironmentPayment = intent.extras!!.getString(CowpayConstantKeys.PaymentEnvironment)!!
        AuthorizationToken = intent.extras!!.getString(CowpayConstantKeys.AuthorizationToken)!!
        MerchantCode = intent.extras!!.getString(CowpayConstantKeys.MerchantCode)!!
        MerchantHashKey = intent.extras!!.getString(CowpayConstantKeys.MerchantHashKey)!!
        MerchantReferenceId = intent.extras!!.getString(CowpayConstantKeys.MerchantReferenceId)!!
        Amount = intent.extras!!.getString(CowpayConstantKeys.Amount)!!
        CustomerMerchantProfileId =
            intent.extras!!.getString(CowpayConstantKeys.CustomerMerchantProfileId)!!
        CustomerName = intent.extras!!.getString(CowpayConstantKeys.CustomerName)!!
        CustomerMobile = intent.extras!!.getString(CowpayConstantKeys.CustomerMobile)!!
        CustomerEmail = intent.extras!!.getString(CowpayConstantKeys.CustomerEmail)!!
        Description = intent.extras!!.getString(CowpayConstantKeys.Description)!!

        //check if save or pay with credit card
        binding.checkboxSaveCardCreditCardActivity.visibility = View.GONE
        /*if (intent.hasExtra(CowpayConstantKeys.CreditCardMethodType)
            && intent.extras!!.getString(CowpayConstantKeys.CreditCardMethodType)!!.compareTo(CowpayConstantKeys.CreditCardMethodSave) == 0
        ) {
            binding.checkboxSaveCardCreditCardActivity.visibility = View.GONE
            binding.btnMakePaymentFawryActivity.text = getString(R.string.save_this_credit_card)
        } else {
            binding.checkboxSaveCardCreditCardActivity.visibility = View.VISIBLE
            binding.btnMakePaymentFawryActivity.text = getString(R.string.make_payment)
        }*/
    }

    override fun onBackPressed() {
        var intent = Intent()
        intent.putExtra(CowpayConstantKeys.PaymentMethod, CowpayConstantKeys.CreditCardMethod)
        if (isOperationSuccess) {
            intent.putExtra(CowpayConstantKeys.ResponseCode, CowpayConstantKeys.SuccessCode)
            intent.putExtra(
                CowpayConstantKeys.ResponseMessage,
                getString(R.string.operation_done_successfully)
            )
            intent.putExtra(
                CowpayConstantKeys.PaymentGatewayReferenceId,
                payment_gateway_reference_id
            )
        } else {
            intent.putExtra(CowpayConstantKeys.ResponseCode, CowpayConstantKeys.ErrorCode)
            intent.putExtra(
                CowpayConstantKeys.ResponseMessage,
                getString(R.string.user_cancelled)
            )
            intent.putExtra(
                CowpayConstantKeys.PaymentGatewayReferenceId,
                payment_gateway_reference_id
            )
        }
        setResult(RESULT_OK, intent)
        finish_activity()
    }

    override fun setListener() {
        binding.layoutRetryCreditCardActivity.setOnClickListener {
            binding.layoutErrorCreditCardActivity.visibility = View.GONE
            binding.layoutContainerCreditCardActivity.visibility = View.VISIBLE
        }

        binding.tvBackHomeCreditCardActivity.setOnClickListener {
            onBackPressed()
        }

        binding.btnMakePaymentFawryActivity.setOnClickListener {
            hideKeyPad(binding.btnMakePaymentFawryActivity)
            if (checkInputs()) {
                //check if save or pay with credit card
                if (intent.hasExtra(CowpayConstantKeys.CreditCardMethodType)
                    && intent.extras!!.getString(CowpayConstantKeys.CreditCardMethodType)!!
                        .compareTo(CowpayConstantKeys.CreditCardMethodSave) == 0
                ) {
                    getPaymentData(false)
                } else {
                    getPaymentData(true)
                }
            }
        }

        binding.edtextCardNumberCreditCardActivity.addTextChangedListener(
            OtherCardTextWatcher(
                binding.edtextCardNumberCreditCardActivity
            )
        )
        binding.edtextCardNumberCreditCardActivity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length == 19) {
                    binding.edtextmonthCreditCardActivity.requestFocus()
                }
            }
        })

        binding.edtextmonthCreditCardActivity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    if (s.toString().length == 1 && s.toString().toInt() in 2..9) {
                        var newText = "0$s"
                        binding.edtextmonthCreditCardActivity.setText(newText)
                        binding.edtextmonthCreditCardActivity.setSelection(newText.length)
                        binding.edtextYearCreditCardActivity.requestFocus()
                    } else if (s.toString().length == 2 && s.toString().toInt() in 1..12) {
                        binding.edtextYearCreditCardActivity.requestFocus()
                    } else if (s.toString().length == 2 && s.toString().toInt() !in 1..12) {
                        var newText = s.toString()[0].toString()
                        binding.edtextmonthCreditCardActivity.setText(newText)
                        binding.edtextmonthCreditCardActivity.setSelection(newText.length)
                    }
                } catch (e: Exception) {
                }
            }
        })

        binding.edtextYearCreditCardActivity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length == 2) {
                    binding.edtextcvvCreditCardActivity.requestFocus()
                }
            }
        })
    }

    fun checkInputs(): Boolean {
        var numberCredit =
            binding.edtextCardNumberCreditCardActivity.text.toString().trim().replace(" ", "")
        var cal = Calendar.getInstance()
        var currentYear = cal.get(Calendar.YEAR)
        var userYear = "20".plus(binding.edtextYearCreditCardActivity.text.toString())
        if (numberCredit.length < 16) {
            binding.edtextCardNumberCreditCardActivity.error =
                getString(R.string.invalid_card_number)
            return false
        } else if (binding.edtextmonthCreditCardActivity.text.toString().length < 2) {
            binding.edtextmonthCreditCardActivity.error = getString(R.string.invalid_month)
            return false
        } else if (binding.edtextYearCreditCardActivity.text.toString().length < 2 || userYear.toInt() < currentYear) {
            binding.edtextYearCreditCardActivity.error = getString(R.string.invalid_year)
            return false
        } else if (binding.edtextcvvCreditCardActivity.text.toString().length < 3) {
            binding.edtextcvvCreditCardActivity.error = getString(R.string.invalid_cvv)
            return false
        }
        return true
    }

    fun showMessage(message: String, isOperationSuccess: Boolean) {
        binding.layoutErrorCreditCardActivity.visibility = View.VISIBLE
        binding.layoutContainerCreditCardActivity.visibility = View.GONE
        var msg = message
        if (isOperationSuccess) {
            binding.ivErrorCreditCardActivity.setImageResource(R.drawable.accepted_icon)
            //show success message
            binding.layoutRetryCreditCardActivity.visibility = View.GONE
            binding.tvErrorCreditCardActivity.setTextColor(
                ContextCompat.getColor(
                    this@CreditCardActivity,
                    R.color.ufogreen
                )
            )
            msg = msg.plus("\n").plus(getString(R.string.returning_to_home_))
            startProgressAnimation()
        } else {
            binding.ivErrorCreditCardActivity.setImageResource(R.drawable.worng_icon)
            //show error message and retry
            binding.layoutRetryCreditCardActivity.visibility = View.VISIBLE
            binding.tvErrorCreditCardActivity.setTextColor(
                ContextCompat.getColor(
                    this@CreditCardActivity,
                    R.color.darkgray
                )
            )
        }
        binding.tvErrorCreditCardActivity.text = msg
    }

    var objectAnimator_progress_confirm: ValueAnimator? = null

    fun startProgressAnimation() {
        objectAnimator_progress_confirm = ObjectAnimator.ofInt(
            0,
            100
        ).setDuration(2000)

        objectAnimator_progress_confirm?.addUpdateListener { valueAnimator: ValueAnimator ->
            val progress = valueAnimator.animatedValue as Int
            if (progress >= 100) {
                if (!isFinishing) {
                    if (objectAnimator_progress_confirm != null)
                        objectAnimator_progress_confirm?.cancel()
                    onBackPressed()
                }
            }
        }
        objectAnimator_progress_confirm?.start()
    }

    fun getSignature(): String {
        var hashData = StringBuilder()
        hashData.append(MerchantCode)
        hashData.append(MerchantReferenceId)
        hashData.append(CustomerMerchantProfileId)
        //remove this line for OTP
//        hashData.append("CARD")
        var price = 0.00
        var priceString = "0.00"
        try {
            price = Amount.toDouble()
            priceString = MathUtils().getDecimalFormat(price)
        } catch (e: Exception) {
            price = 0.00
        }
        hashData.append(priceString)
        hashData.append(MerchantHashKey)

        var signature = Hasher().hash256(hashData.toString())
        return signature
    }

    fun getItems(): String {
        var itemModels = ArrayList<ItemModel>()
        var itemModel = ItemModel()
        itemModel.itemId = CustomerMerchantProfileId
        itemModel.description = "example description"
        itemModel.quantity = "1"

        itemModel.price = getAmountInDecimalFormat()

        itemModels.add(itemModel)

        var items = JsonParser().convertItemModelsToJson(itemModels)!!
        return items
    }

    fun getAmountInDecimalFormat(): String {
        var price = 0.00
        var priceString = "0.00"
        try {
            price = Amount.toDouble()
            priceString = MathUtils().getDecimalFormat(price)
        } catch (e: Exception) {
            price = 0.00
        }
        return priceString
    }

    var isOperationSuccess = false
    var payment_gateway_reference_id = ""

    fun getPaymentData(isPay: Boolean) {
        var urlFunction = ""
        if (isPay)
            urlFunction = URL().getChargeUsingCreditCardUrl()
        else
            urlFunction = URL().getGenerateCardAuthTokenUrl()

        var params: MultipartBody.Builder = MultipartBody.Builder()
        //get the default params
        params.setType(MultipartBody.FORM)
        params.addFormDataPart("merchant_code", MerchantCode)
        params.addFormDataPart("customer_name", CustomerName)
        params.addFormDataPart("customer_mobile", CustomerMobile)
        params.addFormDataPart("customer_email", CustomerEmail)
        var cardNumber =
            binding.edtextCardNumberCreditCardActivity.text.toString().trim().replace(" ", "")
        params.addFormDataPart("card_number", cardNumber)
        params.addFormDataPart("expiry_year", binding.edtextYearCreditCardActivity.text.toString())
        params.addFormDataPart(
            "expiry_month",
            binding.edtextmonthCreditCardActivity.text.toString()
        )
        params.addFormDataPart("cvv", binding.edtextcvvCreditCardActivity.text.toString())
        params.addFormDataPart("customer_merchant_profile_id", CustomerMerchantProfileId)
        if (isPay) {
            params.addFormDataPart("merchant_reference_id", MerchantReferenceId)
            params.addFormDataPart("amount", getAmountInDecimalFormat())
            params.addFormDataPart("currency_code", "EGP")

            params.addFormDataPart("description", "example description")
            params.addFormDataPart("signature", getSignature())
            params.addFormDataPart(
                "save_card",
                if (binding.checkboxSaveCardCreditCardActivity.isChecked) "1" else "0"
            )

            //remove this two line for OTP
//            params.addFormDataPart("payment_method", "CARD")
//            params.addFormDataPart("charge_items", getItems())
        }

        var isLive =
            EnvironmentPayment != null && EnvironmentPayment.compareTo(CowpayConstantKeys.Production) == 0
        ConnectionHandler.getInstance()
            .startPostMethod(
                isLive,
                AuthorizationToken,
                urlFunction,
                params.build(),
                object : ConnectionCallback {
                    override fun onStartConnection() {
                        showProgressDialog()
                        isOperationSuccess = false
                    }

                    override fun onFailureConnection(errorMessage: String?) {
                        try {
                            dismissProgressDialog()
                            var parentResponseModel =
                                JsonParser().getParentResponseModel(errorMessage)
                            showMessage(parentResponseModel!!.status_description, false)
                        } catch (e: Exception) {
                            if (Connection.isInternetAvailable(this@CreditCardActivity) && errorMessage != null && errorMessage != "") {
                                showMessage(errorMessage!!, false)
                            } else {
                                showMessage(getString(R.string.server_connection_failed), false)
                            }
                        }
                    }

                    override fun onSuccessConnection(response: String?) {
                        try {
                            dismissProgressDialog()
                            var parentResponseModel = JsonParser().getParentResponseModel(response)
                            if (parentResponseModel != null) {
                                if (parentResponseModel.status_code.compareTo("200") == 0) {
                                    if (parentResponseModel.three_d_secured) {
                                        //check if require 3d secure verification
                                        //generate three d secure Url
                                        var threeDUrl = ""
                                        if (isLive) {
                                            threeDUrl =
                                                URL().baseUrlLive + URL().getGenerateThreeDSecureUrl(
                                                    parentResponseModel.cowpay_reference_id
                                                )
                                        } else {
                                            threeDUrl =
                                                URL().baseUrlStaging + URL().getGenerateThreeDSecureUrl(
                                                    parentResponseModel.cowpay_reference_id
                                                )
                                        }
                                        var nextIntent = Intent(
                                            this@CreditCardActivity,
                                            ThreeDSecureActivity::class.java
                                        )
                                        nextIntent.putExtra("threeDUrl", threeDUrl)
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
                                        startActivityForResult(
                                            nextIntent,
                                            CowpayConstantKeys.RequestCodeThreeDSecureActivity
                                        )
                                        overridePendingTransition(
                                            R.anim.slide_from_right_to_left,
                                            R.anim.slide_in_left
                                        )
                                    } else {
                                        isOperationSuccess = true
                                        if (isPay)
                                            payment_gateway_reference_id =
                                                parentResponseModel.payment_gateway_reference_id
                                        showMessage(parentResponseModel.status_description, true)
                                    }
                                } else {
                                    showMessage(parentResponseModel!!.status_description, false)
                                }
                            } else {
                                showMessage(getString(R.string.operation_failed), false)
                            }
                        } catch (e: Exception) {
                            showMessage(getString(R.string.operation_failed), false)
                        }
                    }
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CowpayConstantKeys.RequestCodeThreeDSecureActivity && data != null && resultCode == Activity.RESULT_OK) {
            var responseCode = data.extras!!.getInt(CowpayConstantKeys.ResponseCode, 0)
            var responseMSG = data.extras!!.getString(CowpayConstantKeys.ResponseMessage)
            isOperationSuccess = responseCode == CowpayConstantKeys.SuccessCode
            payment_gateway_reference_id =
                data.extras!!.getString(CowpayConstantKeys.PaymentGatewayReferenceId)!!
            showMessage(responseMSG!!, isOperationSuccess)
        }
    }

}