package com.alyak.detector.data.api

import com.alyak.detector.data.dto.user.CodeValidateRequest
import com.alyak.detector.data.dto.user.SignInRequest
import com.alyak.detector.data.dto.user.SignInResponse
import com.alyak.detector.data.dto.user.SignUpRequest
import com.alyak.detector.data.dto.user.SignUpResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {
    @POST("/api/auth/signup")
    suspend fun signUp(
        @Body request: SignUpRequest
    ): SignUpResponse

    @POST("/api/email/send")
    suspend fun requestCode(
        @Query("email") email: String
    )

    @POST("/api/email/verify")
    suspend fun verifyCode(
        @Body request: CodeValidateRequest
    )

    @POST("/api/auth/login")
    suspend fun signIn(
        @Body request: SignInRequest
    ): SignInResponse
}