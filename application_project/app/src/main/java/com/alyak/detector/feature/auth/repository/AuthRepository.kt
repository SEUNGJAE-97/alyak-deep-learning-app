package com.alyak.detector.feature.auth.repository

import com.alyak.detector.core.network.ApiResult
import com.alyak.detector.core.network.safeCall
import com.alyak.detector.feature.auth.data.api.AuthApi
import com.alyak.detector.feature.auth.data.model.TempLoginResponse
import com.alyak.detector.feature.auth.data.model.response.LoginResponse

class AuthRepository(
    private val authApi: AuthApi
) {
    suspend fun kakaoLogin(): ApiResult<LoginResponse> =
        safeCall { authApi.kakaoLogin() }

    suspend fun googleLogin(): ApiResult<LoginResponse> =
        safeCall { authApi.googleLogin() }

    suspend fun tempLogin(): ApiResult<TempLoginResponse> =
        safeCall { authApi.tempLogin() }
}