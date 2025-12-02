package com.alyak.detector.feature.auth.ui.signIn.state

sealed class ContentState(val route: String) {
    object Login : ContentState("login")
    object SignUp : ContentState("signup")
    object FindPassword : ContentState("find_password")
}