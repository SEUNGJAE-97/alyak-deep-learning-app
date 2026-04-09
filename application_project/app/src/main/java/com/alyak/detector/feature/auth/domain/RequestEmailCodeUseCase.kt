package com.alyak.detector.feature.auth.domain

import com.alyak.detector.core.network.ApiResult
import com.alyak.detector.core.network.safeCall
import com.alyak.detector.feature.auth.data.api.AuthApi
import javax.inject.Inject

class RequestEmailCodeUseCase @Inject constructor(
    private val authApi: AuthApi,
) {
    suspend operator fun invoke(
        email: String,
        mode: EmailVerificationMode,
    ): ApiResult<Unit> = safeCall {
        when (mode) {
            EmailVerificationMode.SIGN_UP -> authApi.requestCode(email)
            EmailVerificationMode.FIND_PASSWORD -> authApi.requestResetCode(email)
        }
    }
}
