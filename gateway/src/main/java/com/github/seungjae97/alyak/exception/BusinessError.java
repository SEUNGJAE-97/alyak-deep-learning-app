package com.github.seungjae97.alyak.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BusinessError {

    // 회원 가입
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "유효하지 않은 이메일 포멧 입니다.");


    private final HttpStatus httpStatus;

    private final String message;
}
