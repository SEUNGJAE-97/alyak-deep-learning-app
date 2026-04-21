package com.github.seungjae97.alyak.alyakapiserver.domain.user.dto;

/**
 * 로그인 사용자 본인 비밀번호 변경 요청.
 *
 * @param newPassword 새 비밀번호(평문 — 서버에서 인코딩)
 */
public record PasswordUpdateRequest(String newPassword) {
}
