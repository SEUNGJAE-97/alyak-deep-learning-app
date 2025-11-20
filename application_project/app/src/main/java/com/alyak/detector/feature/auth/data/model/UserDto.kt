package com.alyak.detector.feature.auth.data.model

import com.kakao.sdk.user.model.Gender

data class UserDto(
    val email: String,
    val password: String,
    val name: String,
    val phoneNumber: String,
    val gender: Gender,
    val residentNumber: String
)

