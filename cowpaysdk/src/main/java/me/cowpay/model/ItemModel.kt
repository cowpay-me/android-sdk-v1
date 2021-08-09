package me.cowpay.model

import java.io.Serializable

class ItemModel : Serializable {

    lateinit var itemId:String
    lateinit var description:String
    lateinit var price:String
    lateinit var quantity:String

}