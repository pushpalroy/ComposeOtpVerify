package com.appmason.jetplayground.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.appmason.jetplayground.R
import com.appmason.jetplayground.ui.navigation.Screen


@Composable
fun SplashScreen(navController: NavHostController) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))
    val logoAnimationState = animateLottieCompositionAsState(
        composition = composition,
        speed = 2f
    )
    if (logoAnimationState.isAtEnd && logoAnimationState.isPlaying) {
        navController.navigate(Screen.Home.route)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        LottieAnimation(
            composition = composition,
            progress = { logoAnimationState.progress }
        )
    }
}