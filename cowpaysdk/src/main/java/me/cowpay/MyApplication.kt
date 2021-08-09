package me.cowpay

import android.app.Application
import android.os.Build
import android.provider.Settings
import java.util.*

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }

    fun getLocal(): String {
        return resources.configuration.locale.toString()
    }

    fun getDeviceId(): String {
        return Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

    fun getDeviceModel(): String {
        return (Build.MANUFACTURER
                + " " + Build.MODEL)
    }

    fun getOSVersion(): String {
        return (Build.VERSION_CODES::class.java.fields[android.os.Build.VERSION.SDK_INT].name)
    }

    fun getDeviceModelNameAndOS(): String {
        return (Build.MANUFACTURER
                + " " + Build.MODEL + " " + Build.VERSION.RELEASE
                + " " + Build.VERSION_CODES::class.java.fields[android.os.Build.VERSION.SDK_INT].name)
    }
}