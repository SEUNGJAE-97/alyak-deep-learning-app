package com.alyak.detector.data.api

import com.alyak.detector.data.dto.user.SignUpRequest
import com.alyak.detector.data.dto.user.SignUpResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/api/auth/signup")
    suspend fun signUp(
        @Body request: SignUpRequest
    ): SignUpResponse
}