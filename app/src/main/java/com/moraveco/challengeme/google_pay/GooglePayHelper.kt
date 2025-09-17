package com.moraveco.challengeme.google_pay

import android.content.Context
import android.net.http.UrlRequest
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.WalletConstants
import org.json.JSONArray
import org.json.JSONObject

class GooglePayHelper(private val context: Context) {

    val paymentsClient: PaymentsClient = Wallet.getPaymentsClient(
        context,
        Wallet.WalletOptions.Builder()
            .setEnvironment(WalletConstants.ENVIRONMENT_TEST) // Change to PRODUCTION for live
            .build()
    )

    // Base configuration for Google Pay
    private val baseCardPaymentMethod = JSONObject().apply {
        put("type", "CARD")
        put("parameters", JSONObject().apply {
            put("allowedAuthMethods", JSONArray().apply {
                put("PAN_ONLY")
                put("CRYPTOGRAM_3DS")
            })
            put("allowedCardNetworks", JSONArray().apply {
                put("AMEX")
                put("DISCOVER")
                put("INTERAC")
                put("JCB")
                put("MASTERCARD")
                put("VISA")
            })
        })
    }

    // Tokenization configuration
    private val tokenizationSpecification = JSONObject().apply {
        put("type", "PAYMENT_GATEWAY")
        put("parameters", JSONObject().apply {
            put("gateway", "example") // Replace with your payment processor
            put("gatewayMerchantId", "exampleGatewayMerchantId") // Replace with your merchant ID
        })
    }

    // Card payment method with tokenization
    private val cardPaymentMethod = baseCardPaymentMethod.apply {
        put("tokenizationSpecification", tokenizationSpecification)
    }

    // Check if Google Pay is available
    fun isReadyToPay(): Task<Boolean> {
        val request = JSONObject().apply {
            put("apiVersion", 2)
            put("apiVersionMinor", 0)
            put("allowedPaymentMethods", JSONArray().apply {
                put(baseCardPaymentMethod)
            })
        }

        val readyToPayRequest = IsReadyToPayRequest.fromJson(request.toString())
        return paymentsClient.isReadyToPay(readyToPayRequest)
    }

    // Create payment data request
    fun createPaymentDataRequest(price: String, currencyCode: String = "CZK"): PaymentDataRequest {
        val paymentDataRequest = JSONObject().apply {
            put("apiVersion", 2)
            put("apiVersionMinor", 0)
            put("allowedPaymentMethods", JSONArray().apply {
                put(cardPaymentMethod)
            })
            put("transactionInfo", JSONObject().apply {
                put("totalPrice", price)
                put("totalPriceStatus", "FINAL")
                put("currencyCode", currencyCode)
            })
            put("merchantInfo", JSONObject().apply {
                put("merchantName", "Your App Name") // Replace with your app name
            })
        }

        return PaymentDataRequest.fromJson(paymentDataRequest.toString())
    }
}

