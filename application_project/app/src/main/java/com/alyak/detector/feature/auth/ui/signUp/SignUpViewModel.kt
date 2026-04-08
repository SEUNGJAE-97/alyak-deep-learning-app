package com.alyak.detector.feature.auth.ui.signUp

import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alyak.detector.core.network.ApiResult
import com.alyak.detector.core.network.safeCall
import com.alyak.detector.feature.auth.data.api.AuthApi
import com.alyak.detector.feature.auth.data.model.CodeValidateRequest
import com.alyak.detector.feature.auth.data.model.SignUpRequest
import com.alyak.detector.feature.auth.data.model.SignUpResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authApi: AuthApi
) : ViewModel() {
    private val _signUpResult = MutableStateFlow<Result<SignUpResponse>?>(null)
    val signUpResult: StateFlow<Result<SignUpResponse>?> = _signUpResult
    private val _state = MutableStateFlow(SignUpState())
    val state: StateFlow<SignUpState> = _state

    private val Context.dataStore by preferencesDataStore(name = "user_prefs")

    fun isEmailFormatValid(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

    /** 이메일 입력 변경 — 주소가 바뀌면 인증 메일 발송·인증 완료 상태 초기화 */
    fun onSignUpEmailInputChanged(newEmail: String) {
        val prev = _state.value
        val emailChanged = newEmail != prev.email
        val verificationLost = prev.emailVerified && emailChanged
        _state.value = prev.copy(
            email = newEmail,
            verificationMailSent = if (emailChanged) false else prev.verificationMailSent,
            emailVerified = if (verificationLost) false else prev.emailVerified
        )
    }

    fun validatePassword(password: String) {
        val pattern =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z0-9$@$!%*#?&]{8,20}$")
        val isValid = pattern.matcher(password).matches()
        _state.value = _state.value.copy(validPassword = isValid)
    }

    fun signUpUser(password: String, name: String, context: Context) {
        val st = _state.value
        if (!st.emailVerified || st.email.isBlank()) return
        val email = st.email
        viewModelScope.launch {
            when (val result = safeCall { authApi.signUp(SignUpRequest(email, password, name)) }) {
                is ApiResult.Success -> {
                    saveSignUpResponse(context, result.data)
                    _signUpResult.value = Result.success(result.data)
                }

                is ApiResult.Error -> {
                    val errorMsg = "오류 ${result.code}: ${result.message}"
                    _signUpResult.value = Result.failure(Exception(errorMsg))
                }

                is ApiResult.Exception -> {
                    _signUpResult.value = Result.failure(result.throwable)
                }
            }
        }
    }

    fun requestCode(email: String) {
        viewModelScope.launch {
            when (val result = safeCall { authApi.requestCode(email) }) {
                is ApiResult.Success -> {
                    _state.value = _state.value.copy(verificationMailSent = true)
                    Log.d("code success : ", result.toString())
                }

                is ApiResult.Error -> {
                    _state.value = _state.value.copy(verificationMailSent = false)
                    val errorMsg = "오류 ${result.code}: ${result.message}"
                    Log.d("code error : ", errorMsg)
                }

                is ApiResult.Exception -> {
                    _state.value = _state.value.copy(verificationMailSent = false)
                    Log.d("code error : ", result.throwable.toString())
                }
            }
        }
    }

    fun verifyCode(email: String, code: String) {
        viewModelScope.launch {
            when (val result = safeCall { authApi.verifyCode(CodeValidateRequest(email, code)) }) {
                is ApiResult.Success -> {
                    _state.value = _state.value.copy(emailVerified = true)
                }

                is ApiResult.Error -> {
                    val errorMsg = "오류 ${result.code}: ${result.message}"
                    Log.d("code error : ", errorMsg)
                }

                is ApiResult.Exception -> {
                    Log.d("code error : ", result.throwable.toString())
                }
            }
        }
    }

    private suspend fun saveSignUpResponse(context: Context, response: SignUpResponse) {
        context.dataStore.edit { prefs ->
            prefs[stringPreferencesKey("access_token")] = response.accessToken
            prefs[stringPreferencesKey("refresh_token")] = response.refreshToken
            prefs[stringPreferencesKey("email")] = response.email
        }
    }
}
