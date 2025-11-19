package com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response;

import lombok.Data;

@Data
public class GoogleUserResponse {
    private String sub;            // 사용자 고유 ID (openid)
    private String name;           // 전체 이름 (profile)
    private String given_name;     // 이름 (profile)
    private String family_name;    // 성 (profile)
    private String picture;        // 프로필 이미지 URL (profile)
    private String email;          // 이메일 (email)
    private boolean email_verified; // 이메일 검증 여부 (email)
    private String locale;         // 지역/언어 (profile)

}