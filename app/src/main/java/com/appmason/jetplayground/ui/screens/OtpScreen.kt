package com.appmason.jetplayground.ui.screens

import android.widget.Toast
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
import com.appmason.jetplayground.ui.components.OtpTextField

@Composable
fun OtpScreen(navController: NavHostController) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp),
        color = Color.White
    ) {
        var otpValue by remember {
            mutableStateOf("")
        }
        val context = LocalContext.current

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