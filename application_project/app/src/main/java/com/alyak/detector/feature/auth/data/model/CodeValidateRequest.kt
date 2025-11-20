package com.alyak.detector.feature.auth.data.model

data class CodeValidateRequest(
    val email: String,
    val code: String
)

