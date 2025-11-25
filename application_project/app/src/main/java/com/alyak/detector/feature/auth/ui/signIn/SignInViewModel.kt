package com.alyak.detector.feature.auth.ui.signIn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alyak.detector.core.auth.TokenManager
import com.alyak.detector.core.network.ApiResult
import com.alyak.detector.core.network.safeCall
import com.alyak.detector.feature.auth.data.api.AuthApi
import com.alyak.detector.feature.auth.data.model.SignInResponse
import com.alyak.detector.feature.auth.ui.signIn.state.SignInState
import com.alyak.detector.feature.auth.ui.signIn.state.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) : ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    val state: StateFlow<SignInState> = _state
    private val _signInResult = MutableStateFlow<Result<SignInResponse>?>(null)
    val signInResult: StateFlow<Result<SignInResponse>?> = _signInResult

    //Login logic
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, loginError = false)

            // ========== 임시: tempLogin 사용 (나중에 실제 signIn으로 교체) ==========
            when (val result = safeCall { authApi.tempLogin() }) {
                is ApiResult.Success -> {
                    val tempLoginResponse = result.data

                    // TempLoginResponse를 받는 오버로딩된 saveToken 사용
                    tokenManager.saveToken(tempLoginResponse)

                    // 사용자 정보 저장 (이메일은 tempLoginResponse에서 가져오거나 파라미터로 받은 email 사용)
                    tokenManager.saveUserInfo(
                        email = tempLoginResponse.email.ifEmpty { email },
                        name = null // 실제 로그인 API에서는 이름도 받아올 수 있음
                    )

                    // TempLoginResponse를 SignInResponse로 변환해서 반환
                    val signInResponse = SignInResponse(
                        accessToken = tempLoginResponse.accessToken,
                        expiresIn = 2592000L,
                        userId = 1L
                    )
                    _signInResult.value = Result.success(signInResponse)

                    _state.value = _state.value.copy(
                        isLoading = false,
                        loginSuccess = true,
                        userInfo = UserInfo(email, password)
                    )
                }

                is ApiResult.Error -> {
                    val errorMsg = "오류 ${result.code}: ${result.message}"
                    _signInResult.value = Result.failure(Exception(errorMsg))
                    _state.value = _state.value.copy(
                        isLoading = false,
                        loginError = true,
                        userInfo = null
                    )
                }

                is ApiResult.Exception -> {
                    _signInResult.value = Result.failure(result.throwable)
                    _state.value = _state.value.copy(
                        isLoading = false,
                        loginError = true,
                        userInfo = null
                    )
                }
            }

            // ========== 원래 signIn 로직 (나중에 사용) ==========
            /*
            when (val result = safeCall { authApi.signIn(SignInRequest(email, password)) }) {
                is ApiResult.Success -> {
                    val signInResponse = result.data
                    
                    tokenManager.saveToken(
                        accessToken = signInResponse.accessToken,
                        expiresIn = signInResponse.expiresIn,
                        userId = signInResponse.userId
                    )
                    
                    // 사용자 정보 저장
                    tokenManager.saveUserInfo(
                        email = email,
                        name = null // 실제 API 응답에 이름이 포함되면 여기서 사용
                    )
                    
                    _signInResult.value = Result.success(signInResponse)
                    _state.value = _state.value.copy(
                        isLoading = false,
                        loginSuccess = true,
                        userInfo = UserInfo(email, password)
                    )
                }

                is ApiResult.Error -> {
                    val errorMsg = "오류 ${result.code}: ${result.message}"
                    _signInResult.value = Result.failure(Exception(errorMsg))
                    _state.value = _state.value.copy(
                        isLoading = false,
                        loginError = true,
                        userInfo = null
                    )
                }

                is ApiResult.Exception -> {
                    _signInResult.value = Result.failure(result.throwable)
                    _state.value = _state.value.copy(
                        isLoading = false,
                        loginError = true,
                        userInfo = null
                    )
                }
            }
            */
        }
    }
}

