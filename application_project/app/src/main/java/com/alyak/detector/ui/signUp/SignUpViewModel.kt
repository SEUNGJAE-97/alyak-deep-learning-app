package com.alyak.detector.ui.signUp

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alyak.detector.data.api.AuthApi
import com.alyak.detector.data.dto.user.SignUpRequest
import com.alyak.detector.data.dto.user.SignUpResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authApi: AuthApi
) : ViewModel() {
    private val _signUpResult = MutableStateFlow<Result<SignUpResponse>?>(null)
    val signUpResult: StateFlow<Result<SignUpResponse>?> = _signUpResult
    private val _state = MutableStateFlow(SignUpState())
    val state: StateFlow<SignUpState> = _state

    fun validateEmail(email: String) {
        val pattern: Pattern = Patterns.EMAIL_ADDRESS
        val isValid = pattern.matcher(email).matches()
        _state.value = _state.value.copy(validEmail = isValid)
    }
    fun validatePassword(password: String) {
        val pattern =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z0-9$@$!%*#?&]{8,20}$")
        val isValid = pattern.matcher(password).matches()
        _state.value = _state.value.copy(validPassword = isValid)
    }
    fun signUpUser(email: String, password: String, name: String) {
        viewModelScope.launch {
            try {
                val response = authApi.signUp(SignUpRequest(email, password, name))
                _signUpResult.value = Result.success(response)
            } catch (e: Exception) {
                _signUpResult.value = Result.failure(e)
            }
        }
    }
}