package me.cowpay.util
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class MathUtils {

    fun getDecimalFormat(number: Double): String {
        try {
            val format = NumberFormat.getNumberInstance(Locale.US) as DecimalFormat
            format.applyPattern("0.00")
            return format.format(number)
        } catch (e: Exception) {
            return "0.00"
        }

    }
}