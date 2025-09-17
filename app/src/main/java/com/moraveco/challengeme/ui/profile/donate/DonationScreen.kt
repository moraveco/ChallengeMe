package com.moraveco.challengeme.ui.profile.donate

import android.app.Activity
import android.net.http.UrlRequest
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.common.api.Status
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.PaymentData
import com.moraveco.challengeme.R
import com.moraveco.challengeme.google_pay.GooglePayHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationScreen(
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as ComponentActivity

    var isGooglePayReady by remember { mutableStateOf(false) }
    val googlePayHelper = remember { GooglePayHelper(context) }

    // Google Pay launcher
    val googlePayLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                result.data?.let { intent ->
                    val paymentData = PaymentData.getFromIntent(intent)
                    handlePaymentSuccess(paymentData)
                }
            }
            Activity.RESULT_CANCELED -> {
                // User canceled
                Log.d("GooglePay", "Payment canceled")
            }
            AutoResolveHelper.RESULT_ERROR -> {
                result.data?.let { intent ->
                    val status = AutoResolveHelper.getStatusFromIntent(intent)
                    if (status != null) {
                        handlePaymentError(status)
                    }
                }
            }
        }
    }

    // Check Google Pay availability on launch
    LaunchedEffect(Unit) {
        googlePayHelper.isReadyToPay().addOnCompleteListener { task ->
            try {
                isGooglePayReady = task.result == true
            } catch (exception: Exception) {
                Log.e("GooglePay", "Error checking Google Pay availability", exception)
            }
        }
    }

    fun requestPayment(amount: Double) {
        val paymentDataRequest = googlePayHelper.createPaymentDataRequest(amount.toString())

        AutoResolveHelper.resolveTask(
            googlePayHelper.paymentsClient.loadPaymentData(paymentDataRequest),
            activity,
            LOAD_PAYMENT_DATA_REQUEST_CODE
        )
    }
    val donationItems = listOf(
        DonationItem(
            title = "Poděkování za kávu",
            description = "Pomáháte nám udržet aplikaci v provozu.",
            price = 49.00
        ),
        DonationItem(
            title = "Přátelský příspěvek",
            description = "Podpora pro základní provozní náklady.",
            price = 149.00
        ),
        DonationItem(
            title = "Podpora v růstu",
            description = "Pomáháte nám rozvíjet nové funkce.",
            price = 399.00
        ),
        DonationItem(
            title = "Hrdina komunity",
            description = "Vaše podpora zlepšuje zážitek uživatelů.",
            price = 799.00
        ),
        DonationItem(
            title = "Vizionářský dar",
            description = "Přibližujete nás k našim dlouhodobým cílům.",
            price = 1990.00
        )
    )


    Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
        }
            Spacer(modifier = Modifier.height(20.dp))

            // App Icon
            Image(painter = painterResource(R.drawable.challengeme), null, modifier = Modifier.size(80.dp).clip(RoundedCornerShape(20.dp)))

            Spacer(modifier = Modifier.height(32.dp))

            // Title
            Text(
                text = "Děkujeme!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            Text(
                text = "Vaše podpora nám pomáhá pokračovat ve vývoji a vylepšování této aplikace!",
                fontSize = 16.sp,
                color = Color(0xFFB0B0B0),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Donation Items
            donationItems.forEach { item ->
                DonationItemCard(
                    item = item,
                    isGooglePayReady = isGooglePayReady,
                    onClick = { requestPayment(item.price) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

}

@Composable
fun DonationItemCard(
    item: DonationItem,
    isGooglePayReady: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A2332)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.description,
                    fontSize = 14.sp,
                    color = Color(0xFFB0B0B0),
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            if (isGooglePayReady) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(23,26,19,255)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${item.price.toInt()},00 Kč",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0, 84, 250)
                        )
                    }
                }
            } else {
                Text(
                    text = "Loading...",
                    fontSize = 12.sp,
                    color = Color(0xFF666666)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DonationPreview() {
    DonationScreen(onClose = {})
}

data class DonationItem(
    val title: String,
    val description: String,
    val price: Double
)

private fun handlePaymentSuccess(paymentData: PaymentData?) {
    paymentData?.let {
        val paymentInformation = it.toJson()
        Log.d("GooglePay", "Payment success: $paymentInformation")

        // Process the payment with your backend
        // The paymentInformation contains the payment token
        // that you need to send to your server for processing
    }
}

private fun handlePaymentError(status: Status) {
    Log.e("GooglePay", "Payment failed: $status")
}

private const val LOAD_PAYMENT_DATA_REQUEST_CODE = 991