package com.alyak.detector.data.dto

data class UserDto(
    val email: String,
    val password: String,
    val name: String,
    val phoneNumber: String,
    val gender: Gender,
    val residentNumber: String
)

enum class Gender { male, female }