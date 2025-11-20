package com.alyak.detector.ui.signUp

/**
 * @constructor : 회원가입시 입력한 데이터의 유효성 체크
 * @Param : validEmail email 규칙을 지켰는지 확인
 * @Param : validPassword password 규칙( 영문, 숫자, 특수문자 중 2가지 글자수는 8~20자 사이)을 지켰는지 확인
 * @Param : duplicatedEmail 동일한 이메일이 존재하는지 확인
 * @Param : duplicatedPassword 비밀번호를 정확하게 입력했는지 확인
 * */

data class SignUpState(
    val validEmail: Boolean = false,
    val validPassword: Boolean = false,
    val duplicatedEmail: Boolean = false,
    val duplicatedPassword: Boolean = false,
    val requestCode: Boolean = false,
)