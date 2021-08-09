package me.cowpay.network_connection

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.cowpay.model.ItemModel
import me.cowpay.model.ParentResponseModel


class JsonParser {

    fun getParentResponseModel(response: String?): ParentResponseModel? {
        try {
            val gson = Gson()
            val type = object : TypeToken<ParentResponseModel>() {

            }.type
            return gson.fromJson(response, type)
        } catch (e1: Exception) {
            e1.printStackTrace()
            return null!!
        }
    }

    fun ConvertParentResponseModelToJson(
        parentResponseModel: ParentResponseModel
    ): String? {
        try {
            var bodyjson = ""
            val gson = Gson()
            bodyjson = gson.toJson(parentResponseModel)
            return bodyjson
        } catch (e1: Exception) {
            e1.printStackTrace()
            return null
        }
    }

    fun convertItemModelsToJson(
        itemModels: ArrayList<ItemModel>
    ): String? {
        try {
            var bodyjson = ""
            val gson = Gson()
            bodyjson = gson.toJson(itemModels)
            return bodyjson
        } catch (e1: Exception) {
            e1.printStackTrace()
            return null
        }
    }

}