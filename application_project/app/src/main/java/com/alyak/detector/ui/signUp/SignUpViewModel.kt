package com.alyak.detector.ui.signUp

import android.util.Patterns
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.regex.Pattern

@HiltViewModel
class SignUpViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(SignUpState())
    val state: StateFlow<SignUpState> = _state

    fun validateEmail(email: String) {
        val pattern: Pattern = Patterns.EMAIL_ADDRESS
        val isValid = pattern.matcher(email).matches()
        _state.value = _state.value.copy(validEmail = isValid)
    }

    fun validatePhoneNumber(phoneNumber: String) {
        val pattern = Pattern.compile("^01[0-9]{1}-?[0-9]{3,4}-?[0-9]{4}$")
        val isValid = pattern.matcher(phoneNumber).matches()
        _state.value = _state.value.copy(validPhoneNumber = isValid)
    }

    fun validatePassword(password: String) {
        val pattern =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z0-9$@$!%*#?&]{8,20}$")
        val isValid = pattern.matcher(password).matches()
        _state.value = _state.value.copy(validPassword = isValid)
    }

    fun validateSSN(ssn: String) {
        val pattern = Pattern.compile("^\\d{6}-[1-4]\\d{6}$")
        val isValid = pattern.matcher(ssn).matches()
        _state.value = _state.value.copy(validSSN = isValid)
    }


}