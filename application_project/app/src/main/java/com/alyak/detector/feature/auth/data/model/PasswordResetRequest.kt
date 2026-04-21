package com.alyak.detector.feature.auth.data.model

data class PasswordResetRequest(
    val email: String,
    val newPassword: String,
)
