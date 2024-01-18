package com.appmason.jetplayground.ui.screens

import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.appmason.jetplayground.otp_verifier.receiver.OTPReceiver
import com.appmason.jetplayground.otp_verifier.receiver.startSMSRetrieverClient
import com.appmason.jetplayground.ui.components.OtpTextField
import com.google.android.gms.auth.api.phone.SmsRetriever

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun OtpScreen(navController: NavHostController) {
    val context = LocalContext.current
    var otpValue by remember { mutableStateOf("") }

    OTPReceiverEffect(
        context = context,
        onOtpChange = { otpValue = it }
    )
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp),
        color = Color.White
    ) {
        OtpTextField(
            modifier = Modifier.padding(top = 80.dp),
            otpText = otpValue,
            shouldCursorBlink = false,
            onOtpTextChange = { value, otpFilled ->
                otpValue = value
                if (otpFilled) {
                    Toast.makeText(context, "OTP $otpValue is filled", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun OTPReceiverEffect(
    context: Context,
    onOtpChange: (String) -> Unit
) {
    LaunchedEffect(Unit) {
        Log.e("OTPReceiverEffect", "SMS retrieval has been started.")
        startSMSRetrieverClient(context)
        val myOTPReceiver = OTPReceiver()

        context.registerReceiver(
            myOTPReceiver,
            IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION),
            Context.RECEIVER_EXPORTED
        )

        myOTPReceiver.init(object : OTPReceiver.OTPReceiveListener {
            override fun onOTPReceived(otp: String?) {
                Log.e("OTPReceiverEffect ", "OTP Received  $otp")
                otp?.let {
                    onOtpChange(it)
                }
                context.unregisterReceiver(myOTPReceiver)
            }

            override fun onOTPTimeOut() {
                Log.e("OTPReceiverEffect ", "Timeout")
            }
        })
    }
}