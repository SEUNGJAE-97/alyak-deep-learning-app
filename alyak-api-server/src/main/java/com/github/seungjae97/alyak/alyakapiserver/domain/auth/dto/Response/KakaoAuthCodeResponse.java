package com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class KakaoAuthCodeResponse {
    private final String authorizationUrl;
}
