package com.github.seungjae97.alyak.alyakapiserver.domain.auth.controller;

import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Request.KakaoAuthCodeRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Request.KakaoAuthTokenRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response.KakaoAuthCodeResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.service.KakaoAuthService;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response.KakoAuthTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth/kakao")
@RequiredArgsConstructor
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;

    @PostMapping("/authorize")
    public KakaoAuthCodeResponse getAuthorizationUrl(@RequestBody KakaoAuthCodeRequest request) {
        String state = UUID.randomUUID().toString();
        String authorizationUrl = kakaoAuthService.buildAuthorizationUrl(request.getRedirectUri(), state);

        return new KakaoAuthCodeResponse(authorizationUrl);
    }

    @PostMapping("/token")
    public KakoAuthTokenResponse getAccessToken(@RequestBody KakaoAuthTokenRequest request) {
        return kakaoAuthService.requestAccessToken(request.getCode(), request.getRedirect_uri());
    }

}
