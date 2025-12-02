package com.alyak.detector.feature.auth.data.model

data class SignUpResponse(
    val accessToken: String,
    val refreshToken: String,
    val email: String
)

