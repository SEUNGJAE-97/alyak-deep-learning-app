package com.alyak.detector.feature.auth.data.model

data class SignInResponse(
    val accessToken: String,
    val expiresIn: Long,
    val userId: Long
)

