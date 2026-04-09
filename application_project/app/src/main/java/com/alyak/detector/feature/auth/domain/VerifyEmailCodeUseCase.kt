package com.alyak.detector.feature.auth.domain

import com.alyak.detector.core.network.ApiResult
import com.alyak.detector.core.network.safeCall
import com.alyak.detector.feature.auth.data.api.AuthApi
import com.alyak.detector.feature.auth.data.model.CodeValidateRequest
import javax.inject.Inject

class VerifyEmailCodeUseCase @Inject constructor(
    private val authApi: AuthApi,
) {
    suspend operator fun invoke(email: String, code: String): ApiResult<Unit> =
        safeCall { authApi.verifyCode(CodeValidateRequest(email, code)) }
}
