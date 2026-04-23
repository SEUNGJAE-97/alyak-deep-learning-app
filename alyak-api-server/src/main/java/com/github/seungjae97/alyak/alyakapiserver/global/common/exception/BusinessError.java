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
    EMAIL_CODE_MISMATCH(HttpStatus.BAD_REQUEST, "인증번호가 일치하지 않습니다."),
    USER_NOT_EXIST(HttpStatus.BAD_REQUEST, "존재하지 않는 유저입니다."),
    EXIST_USER(HttpStatus.BAD_REQUEST, "이미 가입한 유저 입니다."),
    KAKAO_LOGIN_ERROR(HttpStatus.BAD_REQUEST, "카카오 로그인에 실패했습니다."),
    ADMIN_LOGIN_FORBIDDEN(HttpStatus.FORBIDDEN, "관리자 권한이 없어 관리자 로그인이 불가합니다."),
    EMAIL_NOT_EXIST(HttpStatus.BAD_REQUEST, "이메일이 비어있습니다."),
    NEW_PASSWORD_REQUIRED(HttpStatus.BAD_REQUEST, "새 비밀번호를 입력해주세요."),
    DONT_EXIST_PILL(HttpStatus.BAD_REQUEST, "존재하지 않는 이름입니다."),
    INVALID_IMAGE_PATH(HttpStatus.BAD_REQUEST, "이미지 경로가 비어있습니다."),
    INVALID_IMAGE_FILE(HttpStatus.BAD_REQUEST, "이미지 파일이 비어있거나 유효하지 않습니다."),
    IMAGE_STORE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 저장에 실패했습니다."),
    LABELING_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "라벨링 항목을 찾을 수 없습니다."),

    // 가족
    DONT_EXIST_FAMILY(HttpStatus.BAD_REQUEST, "존재하는 가족이 없습니다."),
    INVITE_SELF_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "본인이 보낸 초대는 수락할 수 없습니다."),
    ALREADY_IN_OTHER_FAMILY(HttpStatus.CONFLICT, "이미 다른 가족에 소속되어 있어 초대를 수락할 수 없습니다."),
    FAMILY_INVITE_EXPIRED_OR_INVALID(HttpStatus.BAD_REQUEST, "유효하지 않거나 만료된 초대입니다.");

    private final HttpStatus httpStatus;

    private final String message;
}
