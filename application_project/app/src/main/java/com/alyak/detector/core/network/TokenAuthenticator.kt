package com.alyak.detector.core.network

import com.alyak.detector.core.auth.TokenManager
import com.alyak.detector.feature.auth.data.api.AuthApi
import com.alyak.detector.feature.auth.data.model.TokenRequest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Response
import okhttp3.Request
import okhttp3.Route
import dagger.Lazy

@Singleton
class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val authApi: Lazy<AuthApi>
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // 1. 이미 갱신 시도를 했는데도 또 401이 온 경우
        if (response.priorResponse != null) return null

        // 2. 동기적으로 새 토큰 요청 (runBlocking 사용)
        return runBlocking {
            val refreshToken = tokenManager.getRefreshToken() ?: return@runBlocking null

            // 서버에 토큰 재발급 요청
            val request = TokenRequest(refreshToken)
            val call = authApi.get().reissue(request)
            val refreshResponse = call.execute()

            if (refreshResponse.isSuccessful && refreshResponse.body() != null) {
                val newTokens = refreshResponse.body()!!

                // 3. 새 토큰 및 사용자 정보 업데이트
                tokenManager.saveToken(
                    accessToken = newTokens.accessToken,
                    expiresIn = 2592000L,
                    userId = newTokens.userId
                )

                // 4. 실패했던 요청에 새 AccessToken을 달아서 재시도
                response.request.newBuilder()
                    .header("Authorization", "Bearer ${newTokens.accessToken}")
                    .build()
            }else{
                tokenManager.clearToken()
                null
            }
        }
    }

    // 토큰을 리셋하고, 로그인 화면으로 이동
    private suspend fun handleLogout() {
        tokenManager.clearToken()
        // 여기서 추가로 로그아웃 이벤트를 쏴서 UI에서 로그인 화면으로 보내야 함
    }
}