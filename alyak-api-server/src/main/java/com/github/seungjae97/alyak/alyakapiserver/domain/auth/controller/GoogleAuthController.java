package com.github.seungjae97.alyak.alyakapiserver.domain.auth.controller;

import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response.GoogleUserResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response.KakaoAuthCodeResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response.KakoAuthTokenResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response.TokenResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.service.GoogleAuthService;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.service.KakaoAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth/google")
@RequiredArgsConstructor
@Tag(name = "02.구글 OAuth", description = "구글 소셜 로그인 API")
public class GoogleAuthController {

    private final GoogleAuthService googleAuthService;

    /**
     * google 리다이렉트 url 호출하는 API
     */
    @PostMapping("/authorize")
    @Operation(summary = "구글 로그인 API", description = "구글 OAUTH2 로그인 API")
    public KakaoAuthCodeResponse getAuthorizationUrl() {
        String state = UUID.randomUUID().toString();
        String authorizationUrl = googleAuthService.buildAuthorizationUrl(state);

        return new KakaoAuthCodeResponse(authorizationUrl);
    }

    /**
     * Google에서 콜백 (인가 코드 수신) → 인가 코드로 토큰 교환
     */
    @GetMapping("/callback")
    public ResponseEntity<TokenResponse> googleCallback(
            @RequestParam String code,
            @RequestParam String state
    ) {
        KakoAuthTokenResponse tokenResponse = googleAuthService.requestAccessToken(code);
        GoogleUserResponse userInfo = googleAuthService.requestUserInfo(tokenResponse.getAccess_token());
        TokenResponse jwtToken = googleAuthService.saveOrUpdateUser(userInfo);

        return ResponseEntity.ok(jwtToken);
    }

}
