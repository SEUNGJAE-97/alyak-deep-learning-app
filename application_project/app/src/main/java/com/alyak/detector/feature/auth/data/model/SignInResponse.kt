package com.alyak.detector.feature.auth.data.model

data class SignInResponse(
    private val accessToken: String,
    private val expiresIn: Long,
    private val userId: Long
)

