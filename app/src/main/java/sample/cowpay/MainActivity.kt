package sample.cowpay

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import me.cowpay.PaymentMethodsActivity
import me.cowpay.util.CowpayConstantKeys
import sample.cowpay.databinding.ActivityMainBinding
import java.security.SecureRandom
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    /**
     * Test card data
     * card number: 5123456789012346
     * month : 05
     * year : 25
     * exp : 123
     */
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.btnSaveCreditCardMainActivity.visibility = View.GONE
        setListener()
    }

    fun setListener() {
        binding.btnPayWithCreditCardMainActivity.setOnClickListener {
            payWithCreditCard()
        }

        binding.btnPayWithFawryMainActivity.setOnClickListener {
            payWithFawry()
        }

        binding.btnSaveCreditCardMainActivity.setOnClickListener {
            //not fully implemented
//            saveCreditCard()
        }

        binding.btnOpenPaymentListMainActivity.setOnClickListener {
            openPaymentList()
        }

        binding.rbSandboxMainActivity.setOnCheckedChangeListener { buttonView, isChecked ->
            paymentEnvironment =
                if (isChecked) CowpayConstantKeys.SandBox else CowpayConstantKeys.Production
        }
    }

    //live auth
//    var authorizationToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjZkODE5ZTM0ZGUyOTQ1ZWMwNzllNDAzYTQ1MjQyYjM2YTczMTk5MTMyYmFjZWE4OWIzYTUwYTVmZDM0YjYxZDA4ZTJhOGY2ZmY2MWNiNzI2In0.eyJhdWQiOiIzIiwianRpIjoiNmQ4MTllMzRkZTI5NDVlYzA3OWU0MDNhNDUyNDJiMzZhNzMxOTkxMzJiYWNlYTg5YjNhNTBhNWZkMzRiNjFkMDhlMmE4ZjZmZjYxY2I3MjYiLCJpYXQiOjE2MDA1MzA1MzYsIm5iZiI6MTYwMDUzMDUzNiwiZXhwIjoxNjMyMDY2NTM2LCJzdWIiOiIxNyIsInNjb3BlcyI6W119.eMjvz3w7FP5eTBRP4NY6bfax4pu4GFTGDhbX6oATz-avlzTXAxBQkL7Rb3XDvgcxLULXHlS7frhIORA095MfLQ"
    //stage auth
    var authorizationToken =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxIiwianRpIjoiMDljMGY3NDYxODZjMDBlYTU2YmUyMGRkNTQ5Mzc3ZjgyZTZlYmQ4NzBjZWJlNjYzOGMzODFlM2ZjYjE4OTZlNWNhYjY4NDllYzM0ZThiYzciLCJpYXQiOiIxNjI4NTQwNDkzLjcyMTQ4OCIsIm5iZiI6IjE2Mjg1NDA0OTMuNzIxNDk0IiwiZXhwIjoiMTY2MDA3NjQ5My43MDk0NzEiLCJzdWIiOiIxNyIsInNjb3BlcyI6W119.CSRzKNQ-rQ8PvO_ZQUO6d533P0YVInv9X86fMQBKqEMf_Lcsp1jSuVpW5Yzz9RGhU2Sozjby-QwNp8NhdtmTag"

    //dev auth
//    var authorizationToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6ImYzZTc2M2ZlMzUyZGU1ZmViYzNhMzYxZTlkZTg0ZmQzOTUxN2QzZWQzYWRjYjJjOWY4Nzg2ZTMyYTM3OWMxZTQwNDE1YjEwYTk4ZmI4ZGRkIn0.eyJhdWQiOiI1IiwianRpIjoiZjNlNzYzZmUzNTJkZTVmZWJjM2EzNjFlOWRlODRmZDM5NTE3ZDNlZDNhZGNiMmM5Zjg3ODZlMzJhMzc5YzFlNDA0MTViMTBhOThmYjhkZGQiLCJpYXQiOjE2MDAxNjM0MTAsIm5iZiI6MTYwMDE2MzQxMCwiZXhwIjoxNjMxNjk5NDEwLCJzdWIiOiIxNyIsInNjb3BlcyI6W119.F_TF0ztB58x_d0xrVvAI1Sv116X_3WeZa3nKzgEglXTea6ofnUCIa-KT-KHQkb7u0n_sqJNz-ClzDBi-dshbCQ"
    //merchant data
    //dev1212
    //GHIu9nk25D5z
    var merchantCode = "dev1212"

    //$2y$10$IcVQiqt7dm4LEAWaNYfo4uYpv8H9qzjnnCfv9VUDO8QkGkej1KmLq
    //dev1212
    var merchantHashKey = "dev1212"
    var paymentEnvironment = CowpayConstantKeys.SandBox

    var amount = "1"
    fun payWithCreditCard() {
        var intent = Intent(this@MainActivity, PaymentMethodsActivity::class.java)
        //choose payment method
        var PaymentMethod = ArrayList<String>()
        PaymentMethod.add(CowpayConstantKeys.CreditCardMethod)
        intent.putExtra(CowpayConstantKeys.PaymentMethod, PaymentMethod)
        intent.putExtra(CowpayConstantKeys.AuthorizationToken, authorizationToken)
        //set environment production or sandBox
        //CowpayConstantKeys.Production or CowpayConstantKeys.SandBox
        intent.putExtra(CowpayConstantKeys.PaymentEnvironment, paymentEnvironment)
        //set locale language
        var locale = CowpayConstantKeys.ARABIC
        intent.putExtra(CowpayConstantKeys.Language, locale)
        CowpayConstantKeys.LanguageCode = locale
        // use pay with credit card
        intent.putExtra(
            CowpayConstantKeys.CreditCardMethodType,
            CowpayConstantKeys.CreditCardMethodPay
        )
        //merchant data
        intent.putExtra(CowpayConstantKeys.MerchantCode, merchantCode)
        intent.putExtra(
            CowpayConstantKeys.MerchantHashKey,
            merchantHashKey
        )
        //order id
        intent.putExtra(CowpayConstantKeys.MerchantReferenceId, getRandomNumber().toString())
        //order price780
        intent.putExtra(CowpayConstantKeys.Amount, amount)
        //user data
        intent.putExtra(CowpayConstantKeys.CustomerName, "John Smith")
        intent.putExtra(CowpayConstantKeys.CustomerMobile, "01234567890")
        intent.putExtra(CowpayConstantKeys.CustomerEmail, "customer@customer.com")
        intent.putExtra(CowpayConstantKeys.Description, "example description - android sdk")
        //user id
        intent.putExtra(CowpayConstantKeys.CustomerMerchantProfileId, "15")
        startActivityForResult(intent, CowpayConstantKeys.PaymentMethodsActivityRequestCode)
    }

    fun getRandomNumber(): Int {
        val r = SecureRandom()
        val Low = 10000
        val High = 1000000000
        return (System.currentTimeMillis() % Integer.MAX_VALUE).toInt() + (r.nextInt(High - Low) + Low)
    }

    fun payWithFawry() {
        var intent = Intent(this@MainActivity, PaymentMethodsActivity::class.java)
        //choose payment method
        var PaymentMethod = ArrayList<String>()
        PaymentMethod.add(CowpayConstantKeys.FawryMethod)

        intent.putExtra(CowpayConstantKeys.PaymentMethod, PaymentMethod)
        intent.putExtra(CowpayConstantKeys.AuthorizationToken, authorizationToken)
        //set environment production or sandBox
        //CowpayConstantKeys.Production or CowpayConstantKeys.SandBox
        intent.putExtra(CowpayConstantKeys.PaymentEnvironment, paymentEnvironment)
        //set locale language
        var locale = CowpayConstantKeys.ENGLISH
        intent.putExtra(CowpayConstantKeys.Language, locale)
        CowpayConstantKeys.LanguageCode = locale
        //merchant data
        intent.putExtra(CowpayConstantKeys.MerchantCode, merchantCode)
        intent.putExtra(
            CowpayConstantKeys.MerchantHashKey,
            merchantHashKey
        )
        //order id
        intent.putExtra(CowpayConstantKeys.MerchantReferenceId, getRandomNumber().toString())
        //order price780
        intent.putExtra(CowpayConstantKeys.Amount, amount)
        //user data
        intent.putExtra(CowpayConstantKeys.CustomerName, "John Smith")
        intent.putExtra(CowpayConstantKeys.CustomerMobile, "+201096545211")
        intent.putExtra(CowpayConstantKeys.CustomerEmail, "customer@customer.com")
        intent.putExtra(CowpayConstantKeys.Description, "example description")
        //user id
        intent.putExtra(CowpayConstantKeys.CustomerMerchantProfileId, "15")
        startActivityForResult(intent, CowpayConstantKeys.PaymentMethodsActivityRequestCode)
    }

    fun saveCreditCard() {
        var intent = Intent(this@MainActivity, PaymentMethodsActivity::class.java)
        //choose payment method
        var PaymentMethod = ArrayList<String>()
        PaymentMethod.add(CowpayConstantKeys.CreditCardMethod)
        intent.putExtra(CowpayConstantKeys.PaymentMethod, PaymentMethod)
        intent.putExtra(CowpayConstantKeys.AuthorizationToken, authorizationToken)
        //set environment production or sandBox
        //CowpayConstantKeys.Production or CowpayConstantKeys.SandBox
        intent.putExtra(CowpayConstantKeys.PaymentEnvironment, paymentEnvironment)
        //set locale language
        var locale = CowpayConstantKeys.ENGLISH
        intent.putExtra(CowpayConstantKeys.Language, locale)
        CowpayConstantKeys.LanguageCode = locale
        // use save credit card
        intent.putExtra(
            CowpayConstantKeys.CreditCardMethodType,
            CowpayConstantKeys.CreditCardMethodSave
        )
        //merchant data
        intent.putExtra(CowpayConstantKeys.MerchantCode, merchantCode)
        intent.putExtra(
            CowpayConstantKeys.MerchantHashKey,
            merchantHashKey
        )
        //order id
        intent.putExtra(CowpayConstantKeys.MerchantReferenceId, getRandomNumber().toString())
        //order price780
        intent.putExtra(CowpayConstantKeys.Amount, amount)
        //user data
        intent.putExtra(CowpayConstantKeys.CustomerName, "John Smith")
        intent.putExtra(CowpayConstantKeys.CustomerMobile, "01234567890")
        intent.putExtra(CowpayConstantKeys.CustomerEmail, "customer@customer.com")
        intent.putExtra(CowpayConstantKeys.Description, "example description")
        //user id
        intent.putExtra(CowpayConstantKeys.CustomerMerchantProfileId, "15")
        startActivityForResult(intent, CowpayConstantKeys.PaymentMethodsActivityRequestCode)
    }

    fun openPaymentList() {
        var intent = Intent(this@MainActivity, PaymentMethodsActivity::class.java)
        //choose payment method
        var PaymentMethod = ArrayList<String>()
        PaymentMethod.add(CowpayConstantKeys.CreditCardMethod)
        PaymentMethod.add(CowpayConstantKeys.FawryMethod)
        intent.putExtra(CowpayConstantKeys.PaymentMethod, PaymentMethod)
        intent.putExtra(CowpayConstantKeys.AuthorizationToken, authorizationToken)
        //set environment production or sandBox
        //CowpayConstantKeys.Production or CowpayConstantKeys.SandBox
        intent.putExtra(CowpayConstantKeys.PaymentEnvironment, paymentEnvironment)
        //set locale language
        var locale = CowpayConstantKeys.ENGLISH
        intent.putExtra(CowpayConstantKeys.Language, locale)
        CowpayConstantKeys.LanguageCode = locale
        //merchant data
        intent.putExtra(CowpayConstantKeys.MerchantCode, merchantCode)
        intent.putExtra(
            CowpayConstantKeys.MerchantHashKey,
            merchantHashKey
        )
        //order id
        intent.putExtra(CowpayConstantKeys.MerchantReferenceId, getRandomNumber().toString())
        //order price780
        intent.putExtra(CowpayConstantKeys.Amount, amount)
        //user data
        intent.putExtra(CowpayConstantKeys.CustomerName, "John Smith")
        intent.putExtra(CowpayConstantKeys.CustomerMobile, "01234567890")
        intent.putExtra(CowpayConstantKeys.CustomerEmail, "customer@customer.com")
        intent.putExtra(CowpayConstantKeys.Description, "example description")
        //user id
        intent.putExtra(CowpayConstantKeys.CustomerMerchantProfileId, "15")
        startActivityForResult(intent, CowpayConstantKeys.PaymentMethodsActivityRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CowpayConstantKeys.PaymentMethodsActivityRequestCode && data != null && resultCode == Activity.RESULT_OK) {
            var responseCode = data.extras!!.getInt(CowpayConstantKeys.ResponseCode, 0)
            var paymentMethod = data.extras!!.getString(CowpayConstantKeys.PaymentMethod)
            if (responseCode == CowpayConstantKeys.ErrorCode) {
                var responseMSG = data.extras!!.getString(CowpayConstantKeys.ResponseMessage)
                Toast.makeText(this@MainActivity, "$responseMSG $paymentMethod", Toast.LENGTH_LONG)
                    .show()
            } else if (responseCode == CowpayConstantKeys.SuccessCode) {
                var responseMSG = data.extras!!.getString(CowpayConstantKeys.ResponseMessage)
                var paymentGatewayReferenceId =
                    data.extras!!.getString(CowpayConstantKeys.PaymentGatewayReferenceId)
                Toast.makeText(
                    this@MainActivity,
                    responseMSG.plus(" $paymentMethod $paymentGatewayReferenceId"),
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }
    }
}
