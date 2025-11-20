package com.alyak.detector.feature.auth.data.api

import com.alyak.detector.feature.auth.data.model.CodeValidateRequest
import com.alyak.detector.feature.auth.data.model.SignInRequest
import com.alyak.detector.feature.auth.data.model.SignInResponse
import com.alyak.detector.feature.auth.data.model.SignUpRequest
import com.alyak.detector.feature.auth.data.model.SignUpResponse
import com.alyak.detector.feature.auth.data.model.TempLoginResponse
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

    @POST("/api/auth/temp-login")
    suspend fun tempLogin(): TempLoginResponse
}

