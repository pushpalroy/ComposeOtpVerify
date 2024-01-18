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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
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

    /**
     * Right now we don't have support for Autofill in Compose.
     * See [com.appmason.jetplayground.ui.components.Autofill]
     * If we have support from future and want user to autofill OTP from keyboard manually,
     * we do not need to fetch OTP automatically using Google SMS Retriever API and in
     * that case, we can totally remove this [OTPReceiverEffect] and let Autofill handle it.
     */
    OTPReceiverEffect(
        context = context,
        onOtpReceived = { otp -> otpValue = otp }
    )

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp),
        color = Color.White
    ) {
        OtpTextField(
            modifier = Modifier.padding(top = 120.dp),
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
    onOtpReceived: (String) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val myOTPReceiver = remember { OTPReceiver() }
    LaunchedEffect(Unit) {
        Log.e("OTPReceiverEffect", "SMS retrieval has been started.")
        startSMSRetrieverClient(context)

        myOTPReceiver.init(object : OTPReceiver.OTPReceiveListener {
            override fun onOTPReceived(otp: String?) {
                Log.e("OTPReceiverEffect ", "OTP Received: $otp")
                otp?.let { onOtpReceived(it) }
                try {
                    Log.e("OTPReceiverEffect ", "Unregistering receiver")
                    context.unregisterReceiver(myOTPReceiver)
                } catch (e: IllegalArgumentException) {
                    Log.e("OTPReceiverEffect ", "Error in registering receiver: ${e.message}}")
                }
            }

            override fun onOTPTimeOut() {
                Log.e("OTPReceiverEffect ", "Timeout")
            }
        })
    }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                try {
                    Log.e("OTPReceiverEffect ", "Lifecycle.Event.ON_RESUME")
                    Log.e("OTPReceiverEffect ", "Registering receiver")
                    context.registerReceiver(
                        myOTPReceiver,
                        IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION),
                        Context.RECEIVER_EXPORTED
                    )
                } catch (e: IllegalArgumentException) {
                    Log.e("OTPReceiverEffect ", "Error in registering receiver: ${e.message}}")
                }
            }
            if (event == Lifecycle.Event.ON_STOP) {
                try {
                    Log.e("OTPReceiverEffect ", "Lifecycle.Event.ON_STOP")
                    Log.e("OTPReceiverEffect ", "Unregistering receiver")
                    context.unregisterReceiver(myOTPReceiver)
                } catch (e: IllegalArgumentException) {
                    Log.e("OTPReceiverEffect ", "Error in unregistering receiver: ${e.message}}")
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            Log.e("OTPReceiverEffect ", "Compose no longer displayed")
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}