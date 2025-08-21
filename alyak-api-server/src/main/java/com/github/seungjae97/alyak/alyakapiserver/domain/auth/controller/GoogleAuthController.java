package com.github.seungjae97.alyak.alyakapiserver.domain.auth.controller;

import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response.KakaoAuthCodeResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.service.GoogleAuthService;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.service.KakaoAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth/google")
@RequiredArgsConstructor
public class GoogleAuthController {

    private final GoogleAuthService googleAuthService;

    /**
     * google 리다이렉트 url 호출하는 API
     */
    @PostMapping("/authorize")
    public KakaoAuthCodeResponse getAuthorizationUrl() {
        String state = UUID.randomUUID().toString();
        String authorizationUrl = googleAuthService.buildAuthorizationUrl(state);

        return new KakaoAuthCodeResponse(authorizationUrl);
    }

    /**
     * Google에서 콜백 (인가 코드 수신) → 인가 코드로 토큰 교환
     */
    @GetMapping("/callback")
    public String googleCallback(
            @RequestParam String code,
            @RequestParam String state
    ) {
        googleAuthService.requestAccessToken(code);

        return null;
    }

}
