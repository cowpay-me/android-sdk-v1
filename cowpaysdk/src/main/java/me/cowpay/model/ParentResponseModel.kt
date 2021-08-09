package me.cowpay.model

open class ParentResponseModel {

    lateinit var error: String
    lateinit var message: String

    var success = false
    var three_d_secured = false
    lateinit var type: String
    lateinit var order: String
    lateinit var message_source: String
    lateinit var message_type: String
    lateinit var callback_type: String
    lateinit var payment_status: String
    lateinit var payment_gateway_reference_id: String
    lateinit var status_code: String
    lateinit var status_description: String

    lateinit var cowpay_reference_id: String
    lateinit var merchant_reference_id: String




}