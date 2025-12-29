package com.alyak.detector.feature.auth.data.api

import com.alyak.detector.feature.auth.data.model.CodeValidateRequest
import com.alyak.detector.feature.auth.data.model.SignInRequest
import com.alyak.detector.feature.auth.data.model.SignInResponse
import com.alyak.detector.feature.auth.data.model.SignUpRequest
import com.alyak.detector.feature.auth.data.model.SignUpResponse
import com.alyak.detector.feature.auth.data.model.TempLoginResponse
import com.alyak.detector.feature.auth.data.model.response.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {
    @POST("/api/auth/signup")
    suspend fun signUp(
        @Body request: SignUpRequest
    ): Response<SignUpResponse>

    @POST("/api/email/send")
    suspend fun requestCode(
        @Query("email") email: String
    ): Response<Unit>

    @POST("/api/email/verify")
    suspend fun verifyCode(
        @Body request: CodeValidateRequest
    ): Response<Unit>

    @POST("/api/auth/login")
    suspend fun signIn(
        @Body request: SignInRequest
    ): Response<SignInResponse>

    @POST("/api/auth/temp-login")
    suspend fun tempLogin(): Response<TempLoginResponse>

    @POST("/auth/google/authorize")
    suspend fun googleLogin() : Response<LoginResponse>

    @POST("/auth/kakao/authorize")
    suspend fun kakaoLogin() : Response<LoginResponse>


}

