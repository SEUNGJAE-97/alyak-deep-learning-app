package com.alyak.detector.data.dto.user

data class SignUpResponse(
    val accessToken: String,
    val refreshToken: String,
    val email: String
)
