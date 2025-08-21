package com.github.seungjae97.alyak.alyakapiserver.domain.auth.controller;

import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response.KakaoAuthCodeResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.service.KakaoAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth/kakao")
@RequiredArgsConstructor
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;

    /**
     * kakao 리다이렉트 url 호출하는 API
     * */
    @PostMapping("/authorize")
    public KakaoAuthCodeResponse getAuthorizationUrl() {
        String state = UUID.randomUUID().toString();
        String authorizationUrl = kakaoAuthService.buildAuthorizationUrl(state);

        return new KakaoAuthCodeResponse(authorizationUrl);
    }

    @GetMapping("/callback")
    public String kakaoCallback(
            @RequestParam("code") String code,
            @RequestParam(value = "state", required = false) String state) {

        // 1. state 검증
        boolean isValidState = kakaoAuthService.validateState(state);
        if (!isValidState) {
            return "Invalid state parameter";
        }

        // 2. 받은 인가 코드(code)를 이용해 토큰 요청을 진행할 수 있도록 다음 처리 호출
        kakaoAuthService.requestAccessToken(code);
        return "Authorization code received: " + code + "state : " + state;
    }
}
