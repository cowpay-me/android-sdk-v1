package me.cowpay.util

object CowpayConstantKeys {
    val PaymentMethodsActivityRequestCode = 100
    val RequestCodeCreditCardActivity = 101
    val RequestCodeFawryActivity = 102
    val RequestCodeThreeDSecureActivity = 104

    //The Api merchant code provided in the merchant's dashboard at Cowpay.
    val MerchantCode = "merchant_code"
    val AuthorizationToken = "authorization_token"

    //The Api Merchant Hash Key provided in the merchant's dashboard at Cowpay.
    val MerchantHashKey = "merchant_hash_key"

    //The unique reference number for the charge request in merchant system. This can be the order ID
    val MerchantReferenceId = "merchant_reference_id"
    val PaymentMethod = "payment_method"
    val PaymentEnvironment = "payment_environment"
    val Production = "production"
    val SandBox = "staging"
    val Language = "localeLanguage"
    val ARABIC = "ar"
    val ENGLISH = "en"
    var LanguageCode = "en"

    val CardNumber = "card_number"

    //Card expiry year in 2 digits format "21".
    val ExpiryYear = "expiry_year"

    //Card expiry month in 2 digits format "07".
    val ExpiryMonth = "expiry_month"

    //Card CVV.
    val CVV = "cvv"
    var SaveCard = "save_card"

    //The unique customer profile ID in merchant system. This can be the user ID.
    val CustomerMerchantProfileId = "customer_merchant_profile_id"
    val CustomerName = "customer_name"
    val CustomerMobile = "customer_mobile"
    val CustomerEmail = "customer_email"

    //The charge amount must in the form of xx.xx
    val Amount = "amount"

    //Only EGP is accepted as value
    val CurrencyCode = "currency_code"
    val Description = "description"
    val ChargeItems = "charge_items"
    val Signature = "signature"


    val CreditCardMethodType = "type"
    val CreditCardMethodPay = "pay"
    val CreditCardMethodSave = "save"
    val CreditCardMethod = "card"
    val FawryMethod = "fawry"

    val SuccessCode = 1
    val ErrorCode = 2

    val ResponseCode = "response_code"
    val ResponseMessage = "response_message"
    val PaymentGatewayReferenceId = "payment_gateway_reference_id"

}