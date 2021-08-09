package me.cowpay.network_connection

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface APIInterface {

    @GET("{function}")
    abstract fun doGetConnection(
        @Header("Authorization") token: String, @Path(
            value = "function",
            encoded = true
        ) function: String, @QueryMap params: MutableMap<String, Any?>
    ): Call<String>

    @POST("{function}")
    abstract fun doPostConnection(
        @Header("Authorization") token: String, @Path(
            value = "function",
            encoded = true
        ) function: String, @Body params: RequestBody
    ): Call<String>
}