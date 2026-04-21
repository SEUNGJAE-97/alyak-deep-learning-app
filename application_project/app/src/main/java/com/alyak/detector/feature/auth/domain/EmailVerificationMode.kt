package com.alyak.detector.feature.auth.domain

/** 이메일 인증번호 발송 API 분기*/
enum class EmailVerificationMode {
    SIGN_UP,
    FIND_PASSWORD,
}
