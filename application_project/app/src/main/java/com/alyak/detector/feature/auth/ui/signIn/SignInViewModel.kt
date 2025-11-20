package com.alyak.detector.feature.auth.ui.signIn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alyak.detector.core.network.ApiResult
import com.alyak.detector.core.network.safeCall
import com.alyak.detector.feature.auth.data.api.AuthApi
import com.alyak.detector.feature.auth.data.model.SignInRequest
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
    private val authApi: AuthApi
) : ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    val state: StateFlow<SignInState> = _state
    private val _signInResult = MutableStateFlow<Result<SignInResponse>?>(null)
    val signUpResult: StateFlow<Result<SignInResponse>?> = _signInResult

    //Login logic
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, loginError = false)

            when (val result = safeCall { authApi.signIn(SignInRequest(email, password)) }) {
                is ApiResult.Success -> {
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
        }
    }
}

