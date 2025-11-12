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
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "이메일 인증을 진행해주세요."),
    USER_NOT_EXIST(HttpStatus.BAD_REQUEST, "존재하지 않는 유저입니다."),
    EMAIL_VERIFICATION_EXPIRED(HttpStatus.BAD_REQUEST, "이메일 인증 시간이 만료되었습니다. 다시 인증해주세요.");

    private final HttpStatus httpStatus;

    private final String message;
}
