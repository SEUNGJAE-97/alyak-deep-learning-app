package com.alyak.detector.feature.auth.data.model

data class TempLoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val email: String
)
