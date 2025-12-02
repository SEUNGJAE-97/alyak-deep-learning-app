package com.alyak.detector.viewModel

import androidx.lifecycle.ViewModel
import com.alyak.detector.feature.auth.ui.signIn.state.UserInfo
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class UserViewModel @Inject constructor() : ViewModel() {
    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo: StateFlow<UserInfo?> = _userInfo

    fun setUserInfo(id: String, password: String, name: String) {
        _userInfo.value = UserInfo(id = id, password = password, name = name)
    }

    fun clearUserInfo() {
        _userInfo.value = null
    }
}