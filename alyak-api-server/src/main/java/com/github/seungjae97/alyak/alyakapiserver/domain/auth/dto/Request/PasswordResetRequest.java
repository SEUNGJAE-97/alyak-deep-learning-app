package com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 비밀번호 찾기: 이메일 인증 완료 후 새 비밀번호 설정 (JWT 불필요) */
@Getter
@Setter
@NoArgsConstructor
public class PasswordResetRequest {
    private String email;
    private String newPassword;
}
