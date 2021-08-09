package me.cowpay

import android.content.Intent
import android.os.Bundle
import android.view.View
import me.cowpay.databinding.ActivityFawryBinding
import me.cowpay.model.ItemModel
import me.cowpay.network_connection.*
import me.cowpay.util.CowpayConstantKeys
import me.cowpay.util.Hasher
import me.cowpay.util.MathUtils
import okhttp3.MultipartBody

class FawryActivity : CowPayBaseActivity(
    R.string.fawry, false, false,
    false, false, false, true, false
) {
    lateinit var binding: ActivityFawryBinding
    override fun doOnCreate(arg0: Bundle?) {
        binding = putContentView(R.layout.activity_fawry) as ActivityFawryBinding
        setAppBarlightAndStatusBarDark(R.color.gold)
        initializeViews()
        setListener()
    }

    override fun initializeViews() {
        getIntentData()
        getPaymentData()
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
    }

    override fun onBackPressed() {
        var intent = Intent()
        intent.putExtra(CowpayConstantKeys.PaymentMethod, CowpayConstantKeys.FawryMethod)
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
        binding.ivBackFawryActivity.setOnClickListener {
            onBackPressed()
        }

        binding.tvCopyCodeFawryActivity.setOnClickListener {
            copyText(payment_gateway_reference_id)
        }

        binding.tvFawryCodeFawryActivity.setOnClickListener {
            copyText(payment_gateway_reference_id)
        }

        binding.btnFinishFawryActivity.setOnClickListener {
            onBackPressed()
        }

        binding.layoutRetryFawryActivity.setOnClickListener {
            getPaymentData()
        }
    }

    fun showError(message: String) {
        binding.layoutErrorFawryActivity.visibility = View.VISIBLE
        binding.layoutLoadingFawryActivity.visibility = View.GONE
        binding.layoutFinishFawryActivity.visibility = View.GONE

        binding.tvErrorFawryActivity.text = message
    }

    fun getSignature(): String {
        var hashData = StringBuilder()
        hashData.append(MerchantCode)
        hashData.append(MerchantReferenceId)
        hashData.append(CustomerMerchantProfileId)
        //remove this line for OTP
//        hashData.append("PAYATFAWRY")
        hashData.append(getAmountInDecimalFormat())
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

    var payment_gateway_reference_id = ""
    fun setData(payment_gateway_reference_id: String) {
        binding.layoutFinishFawryActivity.visibility = View.VISIBLE
        binding.layoutErrorFawryActivity.visibility = View.GONE
        binding.layoutLoadingFawryActivity.visibility = View.GONE

        binding.tvAmountFawryActivity.text = getAmountInDecimalFormat()

        this.payment_gateway_reference_id = payment_gateway_reference_id
        binding.tvFawryCodeFawryActivity.text = payment_gateway_reference_id
        isOperationSuccess = true
    }

    var isOperationSuccess = false

    fun getPaymentData() {
        val urlFunction = URL().getChargeUsingPayAtFawryUrl()
        var params: MultipartBody.Builder = MultipartBody.Builder()
        //get the default params
        params.setType(MultipartBody.FORM)
        params.addFormDataPart("merchant_code", MerchantCode)
        params.addFormDataPart("merchant_reference_id", MerchantReferenceId)
        params.addFormDataPart("customer_merchant_profile_id", CustomerMerchantProfileId)
        params.addFormDataPart("customer_name", CustomerName)
        params.addFormDataPart("customer_mobile", CustomerMobile)
        params.addFormDataPart("customer_email", CustomerEmail)
        params.addFormDataPart("amount", getAmountInDecimalFormat())
        params.addFormDataPart("currency_code", "EGP")
        params.addFormDataPart("description", Description)
        params.addFormDataPart("signature", getSignature())

        //remove this two line for OTP
//        params.addFormDataPart("payment_method", "PAYATFAWRY")
//        params.addFormDataPart("charge_items", getItems())

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
                        binding.layoutFinishFawryActivity.visibility = View.GONE
                        binding.layoutErrorFawryActivity.visibility = View.GONE
                        binding.layoutLoadingFawryActivity.visibility = View.VISIBLE
                        isOperationSuccess = false
                    }

                    override fun onFailureConnection(errorMessage: String?) {
                        try {
                            var parentResponseModel =
                                JsonParser().getParentResponseModel(errorMessage)
                            showError(parentResponseModel!!.status_description)
                        } catch (e: Exception) {
                            if (Connection.isInternetAvailable(this@FawryActivity) && errorMessage != null && errorMessage != "") {
                                showError(errorMessage!!)
                            } else {
                                showError(getString(R.string.server_connection_failed))
                            }
                        }
                    }

                    override fun onSuccessConnection(response: String?) {
                        try {
                            var parentResponseModel = JsonParser().getParentResponseModel(response)
                            if (parentResponseModel != null) {
                                if (parentResponseModel.status_code.compareTo("200") == 0) {
                                    setData(parentResponseModel.payment_gateway_reference_id)
                                } else {
                                    showError(parentResponseModel!!.status_description)
                                }
                            } else {
                                showError(getString(R.string.server_connection_failed))
                            }
                        } catch (e: Exception) {
                            showError(getString(R.string.server_connection_failed))
                        }
                    }
                })
    }
}