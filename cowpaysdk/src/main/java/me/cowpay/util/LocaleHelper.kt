package me.cowpay.util

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import android.util.Log
import java.util.*

object LocaleHelper {
    fun updateLocale(base: Context, local: String): Context {
        local.let {
            return if (it.isNotEmpty()) {
                updateResources(base, it)
            } else {
                base
            }
        }
    }

    fun applyOverrideConfiguration(
        base: Context,
        overrideConfiguration: Configuration?
    ): Configuration? {
        if (overrideConfiguration != null && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//        if (overrideConfiguration != null && Build.VERSION.SDK_INT in 21..25) {
            val uiMode = overrideConfiguration.uiMode
            overrideConfiguration.setTo(base.resources.configuration)
            overrideConfiguration.uiMode = uiMode
        }
        return overrideConfiguration
    }

    private fun updateResources(base: Context, language: String): Context {
        var newBase = base
        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration = base.resources.configuration
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            configuration.setLocale(locale)
            configuration.setLayoutDirection(locale)
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            configuration.setLocales(localeList)

            newBase.resources.updateConfiguration(configuration, base.resources.displayMetrics)
            newBase = newBase.createConfigurationContext(configuration)
        } else {
            configuration.setLocale(locale)
            configuration.setLayoutDirection(locale)
            newBase.resources.updateConfiguration(configuration, base.resources.displayMetrics)
            newBase = newBase.createConfigurationContext(configuration)
        }
        return newBase
    }
}