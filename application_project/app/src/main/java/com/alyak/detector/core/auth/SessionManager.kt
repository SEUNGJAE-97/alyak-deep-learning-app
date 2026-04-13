package com.alyak.detector.core.auth

import com.alyak.detector.di.ApplicationScope
import com.alyak.detector.feature.auth.data.model.TempLoginResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val tokenManager: TokenManager,
    @ApplicationScope private val externalScope: CoroutineScope
) {
    private val _userSession = MutableStateFlow<UserSession>(UserSession.Loading)
    val userSession: StateFlow<UserSession> = _userSession.asStateFlow()

    init {
        externalScope.launch {
            combine(
                tokenManager.accessTokenFlow,
                tokenManager.userEmailFlow,
                tokenManager.userNameFlow
            ) { token, email, name ->
                if (token != null && email != null && name != null) {
                    UserSession.Authenticated(UserInfo(email, name))
                } else {
                    UserSession.UnAuthenticated
                }
            }.collect { session ->
                _userSession.value = session
            }
        }
    }
    // 로그인 시 호출
    suspend fun login(response: TempLoginResponse, email: String, name: String) {
        tokenManager.saveToken(response)
        tokenManager.saveUserInfo(email, name)
    }

    // 로그아웃 시 호출
    suspend fun logout() {
        tokenManager.clearToken()
    }
}

// 유저 상태 클래스
sealed class UserSession {
    object Loading : UserSession()
    data class Authenticated(val userInfo: UserInfo) : UserSession()
    object UnAuthenticated : UserSession()
}

data class UserInfo(val email: String, val name: String)