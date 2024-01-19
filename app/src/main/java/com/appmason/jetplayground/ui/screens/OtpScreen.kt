package com.appmason.jetplayground.ui.screens

import android.app.Activity
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.navigation.NavHostController
import com.appmason.jetplayground.R
import com.appmason.jetplayground.otp_verifier.receiver.OTPReceiver
import com.appmason.jetplayground.otp_verifier.receiver.startSMSRetrieverClient
import com.appmason.jetplayground.ui.components.OtpInputField
import com.google.android.gms.auth.api.phone.SmsRetriever

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun OtpScreen(navController: NavHostController) {
    val context = LocalContext.current
    var otpValue by remember { mutableStateOf("") }
    var isOtpFilled by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    /**
     * Right now we don't have support for Autofill in Compose.
     * See [com.appmason.jetplayground.ui.components.Autofill] for some temporary solutions.
     *
     * If we have support in the future and want user to autofill OTP from keyboard manually,
     * then we do not need to fetch OTP automatically using Google SMS Retriever API and in
     * that case, we can totally remove this [OtpReceiverEffect] and let Autofill handle it.
     * But Google SMS Retriever API is a great way anyways to fetch and populate OTP!
     */
    OtpReceiverEffect(
        context = context,
        onOtpReceived = { otp ->
            otpValue = otp
            if (otpValue.length == 6) {
                keyboardController?.hide()
                isOtpFilled = true
            }
        }
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    /**
     * Set status bar color for this screen
     */
    (LocalView.current.context as Activity).window.statusBarColor = Color.White.toArgb()

    /**
     * OTP Screen UI starts here
     */
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier
                    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                    .drawWithContent {
                        drawContent()
                    },
                navigationIcon = {
                    Box(
                        Modifier
                            .size(48.dp)
                            .clickable { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            tint = Color.DarkGray,
                            contentDescription = "Back",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.DarkGray,
                    actionIconContentColor = Color.DarkGray
                ),
                title = { Text(text = "Enter One Time Password") },
                windowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
            )
        },
        bottomBar = {
            Button(
                onClick = {},
                enabled = isOtpFilled,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
            ) {
                Text(text = "Continue")
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(24.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp, 0.dp),
                    text = "Please verify your phone number with the OTP we sent to (***)***-2193.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center
                )
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(24.dp),
                    color = Color.White
                ) {
                    OtpInputField(
                        modifier = Modifier
                            .padding(top = 48.dp)
                            .focusRequester(focusRequester),
                        otpText = otpValue,
                        shouldCursorBlink = false,
                        onOtpModified = { value, otpFilled ->
                            otpValue = value
                            isOtpFilled = otpFilled
                            if (otpFilled) {
                                keyboardController?.hide()
                            }
                        }
                    )
                }
            }
        }
    }

}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun OtpReceiverEffect(
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