package com.alyak.detector.ui.signIn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alyak.detector.ui.signIn.state.SignInState
import com.alyak.detector.ui.signIn.state.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SignInViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    val state: StateFlow<SignInState> = _state

    //Login logic
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, loginError = false)

            if (email == "1234" && password == "1234") {
                _state.value = _state.value.copy(
                    isLoading = false,
                    loginSuccess = true,
                    userInfo = UserInfo(email, password)
                )
            } else {
                _state.value = _state.value.copy(
                    isLoading = false,
                    loginError = true,
                    userInfo = null
                )
            }
        }
    }
}

