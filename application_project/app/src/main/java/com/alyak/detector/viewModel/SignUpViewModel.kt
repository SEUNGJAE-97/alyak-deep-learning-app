package com.alyak.detector.viewModel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.regex.Pattern

/**
 * @constructor : 회원가입시 입력한 데이터의 유효성 체크

 * @Param : validPhoneNumber 유효한 휴대폰 번호 포맷인지 확인
 * @Param : validBirthday 현재 시간보다 미래의 시간을 입력했는지 확인
 * @Param : validEmail email 규칙을 지켰는지 확인
 * @Param : validPassword password 규칙( 영문, 숫자, 특수문자 중 2가지 글자수는 8~20자 사이)을 지켰는지 확인
 * @Param : duplicatedEmail 동일한 이메일이 존재하는지 확인
 * @Param : duplicatedPassword 비밀번호를 정확하게 입력했는지 확인
 * @Param : duplicatedPhoneNumber 동일한 휴대폰 번호가 존재하는지 확인
 * */

data class SignUpState(
    val validPhoneNumber: Boolean = false,
    val validSSN: Boolean = false,
    val validEmail: Boolean = false,
    val validPassword: Boolean = false,

    // DB와 연결 후 체크하는 로직 必
    val duplicatedEmail: Boolean = false,
    val duplicatedPassword: Boolean = false,
    val duplicatedPhoneNumber: Boolean = false
)

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