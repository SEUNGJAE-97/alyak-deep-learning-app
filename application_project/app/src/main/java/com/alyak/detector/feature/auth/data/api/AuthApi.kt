package com.alyak.detector.feature.auth.data.api

import com.alyak.detector.feature.auth.data.model.CodeValidateRequest
import com.alyak.detector.feature.auth.data.model.PasswordResetRequest
import com.alyak.detector.feature.auth.data.model.SignInRequest
import com.alyak.detector.feature.auth.data.model.SignInResponse
import com.alyak.detector.feature.auth.data.model.SignUpRequest
import com.alyak.detector.feature.auth.data.model.SignUpResponse
import com.alyak.detector.feature.auth.data.model.TempLoginResponse
import com.alyak.detector.feature.auth.data.model.TokenRequest
import com.alyak.detector.core.network.NoAuth
import com.alyak.detector.feature.auth.data.model.response.LoginResponse
import com.alyak.detector.feature.auth.data.model.response.TokenResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query


interface AuthApi {
    @NoAuth
    @POST("/api/auth/signup")
    suspend fun signUp(
        @Body request: SignUpRequest
    ): Response<SignUpResponse>

    @NoAuth
    @POST("/api/email/send")
    suspend fun requestCode(
        @Query("email") email: String
    ): Response<Unit>

    @NoAuth
    @POST("/api/email/send/reset")
    suspend fun requestResetCode(
        @Query("email") email: String
    ): Response<Unit>

    @NoAuth
    @POST("/api/email/verify")
    suspend fun verifyCode(
        @Body request: CodeValidateRequest
    ): Response<Unit>

    @NoAuth
    @POST("/api/auth/password/reset")
    suspend fun resetPassword(
        @Body request: PasswordResetRequest
    ): Response<Unit>

    @NoAuth
    @POST("/api/auth/login")
    suspend fun signIn(
        @Body request: SignInRequest
    ): Response<SignInResponse>

    @NoAuth
    @POST("/api/auth/temp-login")
    suspend fun tempLogin(): Response<TempLoginResponse>

    @NoAuth
    @POST("/auth/google/authorize")
    suspend fun googleLogin() : Response<LoginResponse>

    @NoAuth
    @POST("/auth/kakao/authorize")
    suspend fun kakaoLogin() : Response<LoginResponse>

    @NoAuth
    @POST("/api/auth/reissue")
    fun reissue(
        @Body request: TokenRequest
    ): Call<TokenResponse>
}

