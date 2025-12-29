package com.alyak.detector.feature.user.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alyak.detector.core.auth.TokenManager
import com.alyak.detector.core.network.ApiResult
import com.alyak.detector.feature.user.data.model.UserEvent
import com.alyak.detector.feature.user.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager
) : ViewModel() {
    private val _event = MutableSharedFlow<UserEvent>()
    val event = _event.asSharedFlow()

    fun logout() {
        viewModelScope.launch {
            val result = userRepository.logoutUser()
            when (result) {
                is ApiResult.Success -> {
                    _event.emit(UserEvent.LogoutSuccess)
                    tokenManager.clearToken()
                }

                is ApiResult.Error -> {
                    _event.emit(UserEvent.Error("로그아웃에 실패했습니다."))
                }

                is ApiResult.Exception -> {
                    _event.emit(UserEvent.Error("네트워크 오류가 발생했습니다."))
                }
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            val result = userRepository.deleteUser()
            when (result) {
                is ApiResult.Success -> {
                    _event.emit(UserEvent.DeleteAccountSuccess)
                    tokenManager.clearToken()
                }
                is ApiResult.Error -> {
                    _event.emit(UserEvent.Error("회원 탈퇴에 실패했습니다."))
                }
                is ApiResult.Exception -> {
                    _event.emit(UserEvent.Error("네트워크 오류가 발생했습니다."))
                }
            }
        }
    }
}