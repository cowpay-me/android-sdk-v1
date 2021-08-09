package me.cowpay.network_connection

class URL {

    internal var baseUrlLive = "https://cowpay.me/api/v1/"

    internal var baseUrlStaging = "https://staging.cowpay.me/api/v1/"

    //dev Url
//    internal var baseUrlStaging = "http://dev.cowpay.me/api/v1/"

    //new APis
    fun getChargeUsingCreditCardUrl(): String {
        var url = "charge/card"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getGenerateThreeDSecureUrl(cowpay_reference_id: String): String {
        //http://dev.cowpay.me/api/v1/3d/sdk/load/2000070
        var url = "3d/sdk/load/$cowpay_reference_id"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getGenerateCardAuthTokenUrl(): String {
        var url = "auth/token"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getChargeUsingPayAtFawryUrl(): String {
        var url = "charge/fawry"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    //old APIs
//    internal var baseUrlLive = "https://cowpay.me/api/fawry/"
//    internal var baseUrlStaging = "https://staging.cowpay.me/api/fawry/"

    fun getChargeRequestUsingCreditCardUrl(): String {
        var url = "charge-request-cc"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getGenerateCardTokenUrl(): String {
        var url = "generate-card-token"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }

    fun getChargeRequestUsingPayAtFawryUrl(): String {
        var url = "charge-request"
        url = url.replace(" ".toRegex(), "%20")
        return url
    }


}