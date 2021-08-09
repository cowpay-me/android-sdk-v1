package me.cowpay.util
import java.util.regex.Matcher
import java.util.regex.Pattern

class EmailValidator {

    private constructor(){
        pattern = Pattern.compile(EMAIL_PATTERN)
    }

    private var pattern: Pattern? = null
    private var matcher: Matcher? = null

    private val EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"


    private object Holder {
        val INSTANCE = EmailValidator()
    }

    companion object {
        val instance: EmailValidator by lazy { Holder.INSTANCE }
    }

    /**
     * Validate hex with regular expression
     *
     * @param hex hex for validation
     * @return true valid hex, false invalid hex
     */
    fun validate(hex: String): Boolean {

        matcher = pattern!!.matcher(hex)
        return matcher!!.matches()

    }
}