package com.alyak.detector.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alyak.detector.core.auth.SessionManager
import com.alyak.detector.feature.auth.data.model.TempLoginResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class UserViewModel @Inject constructor(
    private val sessionManager : SessionManager
) : ViewModel(){
    val userSession = sessionManager.userSession

    // 로그인
    fun Login(response: TempLoginResponse, email: String, name: String){
        viewModelScope.launch {
            sessionManager.login(response, email, name)
        }
    }

    // 로그아웃
    fun logout(){
        viewModelScope.launch {
            sessionManager.logout()
        }
    }
}