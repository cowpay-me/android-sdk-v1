package me.cowpay.network_connection

import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.security.cert.CertificateException
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class ConnectionHandler {
    companion object {
        fun getInstance(): ConnectionHandler = ConnectionHandler()
    }

    private var retrofit: Retrofit? = null

    internal fun getClient(isLive: Boolean): Retrofit? {
        var baseUrl = ""
        if (isLive)
            baseUrl = URL().baseUrlLive
        else
            baseUrl = URL().baseUrlStaging
        retrofit = Retrofit.Builder().baseUrl(baseUrl).client(
            getUnsafeOkHttpClient().connectTimeout(40, TimeUnit.HOURS)
                .readTimeout(60, TimeUnit.MINUTES)
                .writeTimeout(60, TimeUnit.MINUTES).build()
        )
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build()

        return retrofit
    }

    internal fun getClientWithCustomUrl(baseUrl: String): Retrofit? {
        retrofit = Retrofit.Builder().baseUrl(baseUrl).client(getUnsafeOkHttpClient().build())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit
    }


    fun getUnsafeOkHttpClient(): OkHttpClient.Builder {
        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                    chain: Array<java.security.cert.X509Certificate>,
                    authType: String
                ) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                    chain: Array<java.security.cert.X509Certificate>,
                    authType: String
                ) {
                }

                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                    return arrayOf()
                }
            })

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("TLS")
//            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())

            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory

            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier { hostname, session -> true }
            return builder
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }


    fun startGetMethodWithCustomUrl(
        isLive: Boolean,
        token: String,
        baseUrl: String,
        urlFinction: String,
        params: MutableMap<String, Any?>,
        connectionCallback: ConnectionCallback
    ) {
        val apiInterface = getClientWithCustomUrl(baseUrl)?.create(APIInterface::class.java)
        val call = apiInterface?.doGetConnection("Bearer $token", urlFinction, params)
        connectionCallback.onStartConnection()
        call?.enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response != null && response.code() == 200 && response.isSuccessful) {
                    connectionCallback.onSuccessConnection(response.body()!!.toString())
                } else if (response?.errorBody() != null) {
                    try {
                        connectionCallback.onFailureConnection(
                            JSONObject(
                                response.errorBody()!!.string()
                            ).toString()
                        )
                    } catch (e: Exception) {
                        connectionCallback.onFailureConnection(response.message().toString())
                    }
                } else {
                    connectionCallback.onFailureConnection(response.message().toString())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                connectionCallback.onFailureConnection(t.message)
            }
        })
    }

    fun startPostMethodUsingRX(isLive: Boolean): APIInterface? {
        val apiInterface = getClient(isLive)?.create(APIInterface::class.java)
        return apiInterface
    }

    fun startGetMethod(
        isLive: Boolean,
        token: String,
        urlFinction: String,
        params: MutableMap<String, Any?>,
        connectionCallback: ConnectionCallback
    ) {
        val apiInterface = getClient(isLive)?.create(APIInterface::class.java)
        val call = apiInterface?.doGetConnection("Bearer $token", urlFinction, params)
        connectionCallback.onStartConnection()
        call?.enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response != null && response.code() == 200 && response.isSuccessful) {
                    connectionCallback.onSuccessConnection(response.body()!!.toString())
                } else if (response?.errorBody() != null) {
                    try {
                        connectionCallback.onFailureConnection(
                            JSONObject(
                                response.errorBody()!!.string()
                            ).toString()
                        )
                    } catch (e: Exception) {
                        connectionCallback.onFailureConnection(response.message().toString())
                    }
                } else {
                    connectionCallback.onFailureConnection(response.message().toString())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                connectionCallback.onFailureConnection(t.message)
            }
        })
    }

    fun startPostMethod(
        isLive: Boolean,
        token: String,
        urlFinction: String,
        params: MultipartBody,
        connectionCallback: ConnectionCallback
    ) {
        val apiInterface = getClient(isLive)?.create(APIInterface::class.java)
        val call = apiInterface?.doPostConnection("Bearer $token", urlFinction, params)
        connectionCallback.onStartConnection()
        call?.enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response != null && response.code() == 200 && response.isSuccessful) {
                    connectionCallback.onSuccessConnection(response.body()!!.toString())
                } else if (response?.errorBody() != null) {
                    try {
                        connectionCallback.onFailureConnection(
                            JSONObject(
                                response.errorBody()!!.string()
                            ).toString()
                        )
                    } catch (e: Exception) {
                        connectionCallback.onFailureConnection(response.message().toString())
                    }
                } else {
                    connectionCallback.onFailureConnection(response.message().toString())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                connectionCallback.onFailureConnection(t.message)
            }
        })
    }

    fun startPostMethodWithGSONParams(
        isLive: Boolean,
        token: String,
        urlFinction: String,
        params: MutableMap<String, Any?>,
        connectionCallback: ConnectionCallback
    ) {
        val apiInterface = getClient(isLive)?.create(APIInterface::class.java)
        val json = Gson().toJson(params)
        val body = RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            json
        )

        val call = apiInterface?.doPostConnection("Bearer $token", urlFinction, body)
        connectionCallback.onStartConnection()
        call?.enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response != null && response.code() == 200 && response.isSuccessful) {
                    connectionCallback.onSuccessConnection(response.body()!!.toString())
                } else if (response?.errorBody() != null) {
                    try {
                        connectionCallback.onFailureConnection(
                            JSONObject(
                                response.errorBody()!!.string()
                            ).toString()
                        )
                    } catch (e: Exception) {
                        connectionCallback.onFailureConnection(response.message().toString())
                    }
                } else {
                    connectionCallback.onFailureConnection(response.message().toString())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                connectionCallback.onFailureConnection(t.message)
            }
        })
    }
}