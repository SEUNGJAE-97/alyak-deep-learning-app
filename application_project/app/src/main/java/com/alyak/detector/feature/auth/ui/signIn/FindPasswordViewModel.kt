package com.alyak.detector.feature.auth.ui.signIn

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alyak.detector.core.network.ApiResult
import com.alyak.detector.core.network.safeCall
import com.alyak.detector.feature.auth.data.api.AuthApi
import com.alyak.detector.feature.auth.data.model.PasswordResetRequest
import com.alyak.detector.feature.auth.domain.EmailVerificationConstants
import com.alyak.detector.feature.auth.domain.EmailVerificationMode
import com.alyak.detector.feature.auth.domain.RequestEmailCodeUseCase
import com.alyak.detector.feature.auth.domain.VerifyEmailCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class FindPasswordViewModel @Inject constructor(
    private val authApi: AuthApi,
    private val requestEmailCodeUseCase: RequestEmailCodeUseCase,
    private val verifyEmailCodeUseCase: VerifyEmailCodeUseCase,
) : ViewModel() {

    private val _timeLeft = MutableStateFlow(EmailVerificationConstants.TIMER_SECONDS)
    val timeLeft: StateFlow<Int> = _timeLeft

    private val _isTimerRunning = MutableStateFlow(false)

    private val _state = MutableStateFlow(FindPasswordState())
    val state: StateFlow<FindPasswordState> = _state

    private val _passwordResetSuccess = MutableStateFlow(false)
    val passwordResetSuccess: StateFlow<Boolean> = _passwordResetSuccess

    fun startTimer() {
        _timeLeft.value = EmailVerificationConstants.TIMER_SECONDS
        _isTimerRunning.value = true
        viewModelScope.launch {
            while (_isTimerRunning.value && _timeLeft.value > 0) {
                delay(1000L)
                _timeLeft.value--
            }
            _isTimerRunning.value = false
        }
    }

    fun isEmailFormatValid(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun onEmailInputChanged(newEmail: String) {
        val prev = _state.value
        val emailChanged = newEmail != prev.email
        _state.value = prev.copy(
            email = newEmail,
            verificationMailSent = if (emailChanged) false else prev.verificationMailSent,
            emailVerified = if (emailChanged) false else prev.emailVerified,
            verifyCodeErrorMessage = if (emailChanged) null else prev.verifyCodeErrorMessage,
            requestCodeErrorMessage = if (emailChanged) null else prev.requestCodeErrorMessage,
        )
    }

    fun clearRequestCodeError() {
        _state.value = _state.value.copy(requestCodeErrorMessage = null)
    }

    fun clearVerifyCodeError() {
        _state.value = _state.value.copy(verifyCodeErrorMessage = null)
    }

    fun requestResetCode(email: String) {
        _state.value = _state.value.copy(requestCodeErrorMessage = null)
        viewModelScope.launch {
            when (val result = requestEmailCodeUseCase(email, EmailVerificationMode.FIND_PASSWORD)) {
                is ApiResult.Success -> {
                    _state.value = _state.value.copy(
                        verificationMailSent = true,
                        requestCodeErrorMessage = null,
                    )
                    startTimer()
                }

                is ApiResult.Error -> {
                    val msg = result.message?.takeIf { it.isNotBlank() }
                        ?: "요청에 실패했습니다. (${result.code})"
                    _state.value = _state.value.copy(
                        verificationMailSent = false,
                        requestCodeErrorMessage = msg,
                    )
                }

                is ApiResult.Exception -> {
                    _state.value = _state.value.copy(
                        verificationMailSent = false,
                        requestCodeErrorMessage = "네트워크 오류가 발생했습니다.",
                    )
                }
            }
        }
    }

    fun verifyCode(email: String, code: String) {
        _state.value = _state.value.copy(verifyCodeErrorMessage = null)
        viewModelScope.launch {
            when (val result = verifyEmailCodeUseCase(email, code)) {
                is ApiResult.Success -> {
                    _state.value = _state.value.copy(
                        emailVerified = true,
                        verifyCodeErrorMessage = null,
                    )
                }

                is ApiResult.Error -> {
                    val msg = result.message?.takeIf { it.isNotBlank() }
                        ?: "인증에 실패했습니다. (${result.code})"
                    _state.value = _state.value.copy(verifyCodeErrorMessage = msg)
                }

                is ApiResult.Exception -> {
                    _state.value = _state.value.copy(
                        verifyCodeErrorMessage = "네트워크 오류가 발생했습니다.",
                    )
                }
            }
        }
    }

    fun isPasswordValid(password: String): Boolean {
        val pattern = Pattern.compile(
            "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z0-9$@$!%*#?&]{8,20}$",
        )
        return pattern.matcher(password).matches()
    }

    fun resetPassword(email: String, newPassword: String) {
        _state.value = _state.value.copy(resetErrorMessage = null)
        _passwordResetSuccess.value = false
        viewModelScope.launch {
            when (val result = safeCall {
                authApi.resetPassword(PasswordResetRequest(email, newPassword))
            }) {
                is ApiResult.Success -> {
                    _passwordResetSuccess.value = true
                }

                is ApiResult.Error -> {
                    val msg = result.message?.takeIf { it.isNotBlank() }
                        ?: "비밀번호 변경에 실패했습니다. (${result.code})"
                    _state.value = _state.value.copy(resetErrorMessage = msg)
                    Log.d("FindPassword", "reset error: ${result.code} $msg")
                }

                is ApiResult.Exception -> {
                    _state.value = _state.value.copy(
                        resetErrorMessage = "네트워크 오류가 발생했습니다.",
                    )
                }
            }
        }
    }

    fun consumePasswordResetSuccess() {
        _passwordResetSuccess.value = false
    }
}
