package com.alyak.detector.feature.family.ui.invitation

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alyak.detector.feature.family.data.api.FamilyApi
import com.alyak.detector.utils.QRUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class FamilyInvitationViewModel @Inject constructor(
    private val familyApi: FamilyApi,
) : ViewModel() {
    // 현재 선택된 초대 방식이나 QR 코드 상태 등을 관리
    private val _uiState = MutableStateFlow<InvitationUiState>(InvitationUiState.Idle)
    val uiState = _uiState.asStateFlow()
    private val _scannedInviteToken = MutableStateFlow<String?>(null)
    val scannedInviteToken = _scannedInviteToken.asStateFlow()

    private val _inviteEmailEvent = MutableSharedFlow<InviteEmailUiEvent>(extraBufferCapacity = 1)
    val inviteEmailEvent = _inviteEmailEvent.asSharedFlow()

    private final val INVITE_TTL_SECONDS = 10
    private var timerJob: Job? = null

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

    // QR 재생성
    fun onClickRegenerate() {
        generateInviteCode()
    }

    fun onQrScanned(token: String) {
        _scannedInviteToken.value = token
        // TODO: 서버에 초대 수락/검증 API 호출로 연결
    }

    fun inviteByEmail(email: String) {
        viewModelScope.launch {
            val trimmed = email.trim()
            if (trimmed.isBlank()) {
                _inviteEmailEvent.emit(InviteEmailUiEvent.Failure("이메일을 입력해 주세요."))
                return@launch
            }
            try {
                val response = familyApi.inviteByEmail(trimmed)
                if (response.isSuccessful && response.body() == true) {
                    _inviteEmailEvent.emit(InviteEmailUiEvent.Success)
                } else {
                    _inviteEmailEvent.emit(
                        InviteEmailUiEvent.Failure(
                            "초대 요청에 실패했습니다. (${response.code()})"
                        )
                    )
                }
            } catch (e: Exception) {
                _inviteEmailEvent.emit(
                    InviteEmailUiEvent.Failure(e.message ?: "네트워크 오류가 발생했습니다.")
                )
            }
        }
    }

    // QR코드를 생성하는 함수
    private fun generateInviteCode() {
        timerJob?.cancel()

        viewModelScope.launch {
            _uiState.value = InvitationUiState.Loading
            try{
                // 1. 서버에 토큰 생성 API 호출
                val mockToken = "TEMP_TOKEN_${System.currentTimeMillis()}"

                // 2. 토큰을 Bitmap으로 변환
                val qrBitmap = QRUtils.createQRCode(mockToken)

                // 3. 상태 업데이트 + 타이머 시작
                _uiState.value = InvitationUiState.Success(
                    inviteCode = qrBitmap,
                    remainingSeconds = INVITE_TTL_SECONDS,
                    isExpired = false
                )
                startCountdown()
            }catch (e : Exception){
                _uiState.value = InvitationUiState.Error("토큰 생성 실패")
            }
        }
    }

    private fun startCountdown() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var remaining = INVITE_TTL_SECONDS
            while (remaining > 0) {
                delay(1_000L)
                remaining--

                val currentState = _uiState.value
                if (currentState is InvitationUiState.Success) {
                    _uiState.value = currentState.copy(
                        remainingSeconds = remaining,
                        isExpired = false
                    )
                } else {
                    // 화면이 바뀌었거나 에러면 타이머 중단
                    break
                }
            }

            // 0초가 되면 만료 상태로 플래그만 변경
            val finalState = _uiState.value
            if (finalState is InvitationUiState.Success) {
                _uiState.value = finalState.copy(
                    remainingSeconds = 0,
                    isExpired = true
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}


sealed class InvitationUiState {
    object Idle : InvitationUiState()
    object Loading : InvitationUiState()
    data class Success(
        val inviteCode: Bitmap,
        val remainingSeconds: Int,
        val isExpired: Boolean
    ) : InvitationUiState()
    data class Error(val message: String) : InvitationUiState()
}
enum class InvitationOption {
    QR_CODE, EMAIL, SMS
}

sealed interface InviteEmailUiEvent {
    data object Success : InviteEmailUiEvent
    data class Failure(val message: String) : InviteEmailUiEvent
}
