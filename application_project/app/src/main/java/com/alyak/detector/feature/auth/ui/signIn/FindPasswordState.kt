package com.alyak.detector.feature.auth.ui.signIn

data class FindPasswordState(
    val email: String = "",
    val verificationMailSent: Boolean = false,
    val emailVerified: Boolean = false,
    val requestCodeErrorMessage: String? = null,
    val verifyCodeErrorMessage: String? = null,
    val resetErrorMessage: String? = null,
)
