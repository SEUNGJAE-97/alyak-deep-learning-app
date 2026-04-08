package com.alyak.detector.feature.auth.ui.signUp

/**
 * @param email 입력·인증·가입 API에 쓰는 동일한 이메일 주소
 * @param verificationMailSent 인증번호 받기(requestCode) API 성공 여부(다음 단계 진입 조건)
 * @param emailVerified 인증번호 검증 성공 여부
 * @param verifyCodeErrorMessage 검증 API 실패 시 서버 ProblemDetail `detail` 등
 * @param validPassword 비밀번호 규칙 충족 여부
 * @param duplicatedPassword 비밀번호 재입력 일치 여부(필요 시 사용)
 */
data class SignUpState(
    val email: String = "",
    val verificationMailSent: Boolean = false,
    val emailVerified: Boolean = false,
    val verifyCodeErrorMessage: String? = null,
    val validPassword: Boolean = false,
    val duplicatedPassword: Boolean = false,
)
