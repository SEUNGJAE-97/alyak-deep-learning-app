package com.alyak.detector.ui.signIn

/**
 * @constructor : 회원가입시 입력한 데이터의 유효성 체크

 * @Param : isLoading 로그인 처리중 일때
 * @Param : loginSuccess 로그인 성공시 true
 * @Param : loginError 로그인 실패시 true
 * */

data class SignInState(
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,
    val loginError: Boolean = false,
    val userInfo: UserInfo? = null
)

data class UserInfo(
    val id: String,
    val password: String,
    val name: String? = null
)