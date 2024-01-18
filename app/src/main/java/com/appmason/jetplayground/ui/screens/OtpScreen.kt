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
import androidx.lifecycle.compose.LifecycleResumeEffect

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
    val otpReceiver = remember { OTPReceiver() }

    /**
     * This function should not be used to listen for Lifecycle.Event.ON_DESTROY because Compose
     * stops recomposing after receiving a Lifecycle.Event.ON_STOP and will never be aware of an
     * ON_DESTROY to launch onEvent.
     *
     * This function should also not be used to launch tasks in response to callback events by way
     * of storing callback data as a Lifecycle.State in a MutableState. Instead, see currentStateAsState
     * to obtain a State that may be used to launch jobs in response to state changes.
     */
    LifecycleResumeEffect {
        // add ON_RESUME effect here
        Log.e("OTPReceiverEffect", "SMS retrieval has been started.")
        startSMSRetrieverClient(context)
        otpReceiver.init(object : OTPReceiver.OTPReceiveListener {
            override fun onOTPReceived(otp: String?) {
                Log.e("OTPReceiverEffect ", "OTP Received: $otp")
                otp?.let { onOtpReceived(it) }
                try {
                    Log.e("OTPReceiverEffect ", "Unregistering receiver")
                    context.unregisterReceiver(otpReceiver)
                } catch (e: IllegalArgumentException) {
                    Log.e("OTPReceiverEffect ", "Error in registering receiver: ${e.message}}")
                }
            }

            override fun onOTPTimeOut() {
                Log.e("OTPReceiverEffect ", "Timeout")
            }
        })
        try {
            Log.e("OTPReceiverEffect ", "Lifecycle.Event.ON_RESUME")
            Log.e("OTPReceiverEffect ", "Registering receiver")
            context.registerReceiver(
                otpReceiver,
                IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION),
                Context.RECEIVER_EXPORTED
            )
        } catch (e: IllegalArgumentException) {
            Log.e("OTPReceiverEffect ", "Error in registering receiver: ${e.message}}")
        }
        onPauseOrDispose {
            // add clean up for work kicked off in the ON_RESUME effect here
            try {
                Log.e("OTPReceiverEffect ", "Lifecycle.Event.ON_PAUSE")
                Log.e("OTPReceiverEffect ", "Unregistering receiver")
                context.unregisterReceiver(otpReceiver)
            } catch (e: IllegalArgumentException) {
                Log.e("OTPReceiverEffect ", "Error in unregistering receiver: ${e.message}}")
            }
        }
    }
}