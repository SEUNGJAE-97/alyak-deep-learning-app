package com.alyak.detector.core.network

import com.alyak.detector.core.auth.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 모든 HTTP 요청에 Bearer 토큰을 자동으로 추가하는 Interceptor
 * 
 * TokenManager에서 토큰을 읽어서 Authorization 헤더에 추가합니다.
 * 토큰이 없거나 유효하지 않으면 헤더를 추가하지 않습니다.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // 토큰이 필요한 요청인지 확인 (인증 API는 제외)
        val isAuthEndpoint = originalRequest.url.encodedPath.startsWith("/api/auth") ||
                originalRequest.url.encodedPath.startsWith("/api/email")

        // 인증 관련 엔드포인트가 아니거나, 로그인 요청이 아닌 경우 토큰 추가
        val token = if (!isAuthEndpoint) {
            // TokenManager의 suspend 함수를 호출하기 위해 runBlocking 사용
            // 주의: runBlocking은 메인 스레드에서 사용 시 ANR을 일으킬 수 있지만,
            // OkHttp의 Interceptor는 백그라운드 스레드에서 실행되므로 안전합니다.
            runBlocking {
                tokenManager.getAccessToken()
            }
        } else {
            null
        }

        // 토큰이 있으면 Authorization 헤더 추가
        val newRequest = if (token != null && token.isNotEmpty()) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(newRequest)
    }
}

