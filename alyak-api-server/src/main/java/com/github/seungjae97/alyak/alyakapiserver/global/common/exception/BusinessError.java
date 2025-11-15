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
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "이메일 인증이 완료되지 않았습니다."),
    EMAIL_VERIFICATION_PENDING(HttpStatus.BAD_REQUEST, "이메일 인증을 진행해주세요."),
    EMAIL_VERIFICATION_REQUEST_NEEDED(HttpStatus.BAD_REQUEST, "이메일 인증을 먼저 진행해주세요."),
    EMAIL_VERIFICATION_EXPIRED(HttpStatus.BAD_REQUEST, "이메일 인증 시간이 만료되었습니다. 다시 인증해주세요."),
    USER_NOT_EXIST(HttpStatus.BAD_REQUEST, "존재하지 않는 유저입니다."),
    EXIST_USER(HttpStatus.BAD_REQUEST, "이미 가입한 유저 입니다."),
    KAKAO_LOGIN_ERROR(HttpStatus.BAD_REQUEST, "카카오 로그인에 실패했습니다."),
    EMAIL_NOT_EXIST(HttpStatus.BAD_REQUEST, "이메일이 비어있습니다."),
    DONT_EXIST_PILL(HttpStatus.BAD_REQUEST, "존재하지 않는 이름입니다.");
    private final HttpStatus httpStatus;

    private final String message;
}
