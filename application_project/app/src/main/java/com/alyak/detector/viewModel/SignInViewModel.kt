package com.alyak.detector.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SignInState(
    val isLoading : Boolean = false,
    val loginSuccess : Boolean = false,
    val loginError : Boolean = false
)

class SignInViewModel : ViewModel(){
    private val _state = MutableStateFlow(SignInState())
    val state : StateFlow<SignInState> = _state

    //Login logic
    fun signIn(email : String , password : String){
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, loginError = false)

            if (email == "1234" && password == "1234"){
                _state.value = _state.value.copy(isLoading = false, loginSuccess = true)
            } else {
                _state.value = _state.value.copy(isLoading = false, loginError = true)
            }
        }
    }
}

