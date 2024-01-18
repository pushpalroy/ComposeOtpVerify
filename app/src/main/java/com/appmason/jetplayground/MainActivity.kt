package com.appmason.jetplayground

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.appmason.jetplayground.otp_verifier.util.AppSignatureHelper
import com.appmason.jetplayground.ui.navigation.SetupNavGraph
import com.appmason.jetplayground.ui.theme.JetPlaygroundTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * For OTP verification
         *
         * For development, once the app signature has is fetched, a OTP can be sent from any device like:
         * "DO NOT SHARE: Your JetPlayground OTP code is 643908. Message ID: j7fPi8fNhTk."
         *
         * Here "j7fPi8fNhTk" is the unique hash for this app which needs to be added below the SMS.
         * Without the correct hash, your app won't receive the message callback. This only needs to be
         * generated once per app and stored.
         *
         * For production app, use keytool to generate the hash:
         * https://developers.google.com/identity/sms-retriever/verify#computing_your_apps_hash_string
         */
        val signatureHelper = AppSignatureHelper(this)
        val appSignatures = signatureHelper.appSignatures
        for (signature in appSignatures) {
            Log.e("Otp", "App Signature for OTP: $signature")
        }

        setContent {
            JetPlaygroundTheme {
                val navController = rememberNavController()
                SetupNavGraph(navController = navController)
            }
        }
    }
}