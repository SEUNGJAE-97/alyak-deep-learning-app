package com.alyak.detector.data.dto.user

data class SignInResponse(
    private val accessToken: String,
    private val expiresIn: Long,
    private val userId: Long
)
