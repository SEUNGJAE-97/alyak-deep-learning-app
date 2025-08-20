package com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoUserResponse {
    private Long id;

    @JsonProperty("connected_at")
    private String connectedAt;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class KakaoAccount {
        private String email;
        private Profile profile;
        @JsonProperty("is_email_valid")
        private Boolean isEmailValid;
        @JsonProperty("is_email_verified")
        private Boolean isEmailVerified;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Profile {
        private String nickname;
        @JsonProperty("profile_image_url")
        private String profileImageUrl;
        @JsonProperty("thumbnail_image_url")
        private String thumbnailImageUrl;
    }
}