package com.alyak.detector.core.network

import com.alyak.detector.core.auth.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Invocation
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Retrofit 요청에 Bearer 토큰을 붙입니다.
 * [NoAuth]가 붙은 메서드는 토큰을 붙이지 않습니다.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val invocation = request.tag(Invocation::class.java)
        val skipBearer = invocation?.method()?.getAnnotation(NoAuth::class.java) != null

        val token = if (!skipBearer) {
            runBlocking {
                tokenManager.getAccessToken()
            }
        } else {
            null
        }

        val newRequest = if (token != null && token.isNotEmpty()) {
            request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }

        return chain.proceed(newRequest)
    }
}
