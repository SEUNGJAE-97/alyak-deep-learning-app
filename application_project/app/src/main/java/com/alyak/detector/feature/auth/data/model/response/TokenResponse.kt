package com.alyak.detector.feature.auth.data.model.response

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val email: String,
    val userId: Long,
    val userName: String
)