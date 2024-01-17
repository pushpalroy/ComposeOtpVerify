package com.appmason.jetplayground.ui.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash_screen")
    data object Home : Screen("home_screen")
}