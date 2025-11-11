package com.github.seungjae97.alyak.alyakapiserver.global.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BusinessError {

    // 회원 가입
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "유효하지 않은 이메일 포멧 입니다."),
    INVALID_LOGIN(HttpStatus.BAD_REQUEST, "이메일, 비밀번호를 다시 확인해주세요"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 이메일 입니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "이메일 인증이 완료되지 않았습니다.");

    private final HttpStatus httpStatus;

    private final String message;
}
