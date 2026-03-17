package com.alyak.detector.feature.family.ui.invitation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class FamilyInvitationViewModel @Inject constructor(
    // private val familyRepository: FamilyRepository
) : ViewModel() {

    // 현재 선택된 초대 방식이나 QR 코드 상태 등을 관리
    private val _uiState = MutableStateFlow<InvitationUiState>(InvitationUiState.Idle)
    val uiState = _uiState.asStateFlow()

    // 초대 옵션 선택
    fun onOptionSelected(option: InvitationOption) {
        when(option){
            InvitationOption.QR_CODE -> {

            }
            InvitationOption.EMAIL -> {

            }
            InvitationOption.SMS -> {

            }
        }
    }

    private fun generateInviteCode() {
        viewModelScope.launch {
            _uiState.value = InvitationUiState.Loading
            /*QR코드 생성*/
            delay(1000)
            _uiState.value = InvitationUiState.Success("FAMILY_1234")
        }
    }
}

sealed class InvitationUiState {
    object Idle : InvitationUiState()
    object Loading : InvitationUiState()
    data class Success(val inviteCode: String) : InvitationUiState()
}
enum class InvitationOption {
    QR_CODE, EMAIL, SMS
}
