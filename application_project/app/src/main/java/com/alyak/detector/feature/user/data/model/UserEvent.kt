package com.alyak.detector.feature.user.data.model

sealed class UserEvent {
    object LogoutSuccess : UserEvent()
    object DeleteAccountSuccess : UserEvent()
    data class Error(val message: String) : UserEvent()
}