package com.alyak.detector.feature.family.ui.invitation

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alyak.detector.utils.QRUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
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
                generateInviteCode()
            }
            InvitationOption.EMAIL -> {

            }
            InvitationOption.SMS -> {

            }
        }
    }

    // QR코드를 생성하는 함수
    private fun generateInviteCode() {
        viewModelScope.launch {
            _uiState.value = InvitationUiState.Loading
            try{
                // 1. 서버에 토큰 생성 API 호출
                val mockToken = "TEMP_TOKEN_${System.currentTimeMillis()}"

                // 2. 토큰을 Bitmap으로 변환
                val qrBitmap = QRUtils.createQRCode(mockToken)
                _uiState.value = InvitationUiState.Success(qrBitmap)

            }catch (e : Exception){
                _uiState.value = InvitationUiState.Error("토큰 생성 실패")
            }
        }
    }
}

sealed class InvitationUiState {
    object Idle : InvitationUiState()
    object Loading : InvitationUiState()
    data class Success(val inviteCode: Bitmap) : InvitationUiState()
    data class Error(val message: String) : InvitationUiState()
}
enum class InvitationOption {
    QR_CODE, EMAIL, SMS
}
