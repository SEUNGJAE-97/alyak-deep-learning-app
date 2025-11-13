package com.github.seungjae97.alyak.alyakapiserver.domain.auth.controller;

import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response.KakaoAuthCodeResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response.KakaoUserResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response.KakoAuthTokenResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response.TokenResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.service.KakaoAuthService;
import com.github.seungjae97.alyak.alyakapiserver.global.common.exception.BusinessError;
import com.github.seungjae97.alyak.alyakapiserver.global.common.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/auth/kakao")
@RequiredArgsConstructor
@Tag(name = "03. 카카오 OAuth", description = "카카오 소셜 로그인 API")
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;

    /**
     * kakao 리다이렉트 url 호출하는 API
     * */
    @PostMapping("/authorize")
    @Operation(summary = "카카오 로그인 호출 API", description = "카카오 OAUTH2 로그인 인증")
    public KakaoAuthCodeResponse getAuthorizationUrl() {
        String state = UUID.randomUUID().toString();
        String authorizationUrl = kakaoAuthService.buildAuthorizationUrl(state);

        return new KakaoAuthCodeResponse(authorizationUrl);
    }

    @GetMapping("/callback")
    @Operation(summary = "카카오 로그인 콜백 API", description = "서버내부에서 호출하는 용도")
    public ResponseEntity<TokenResponse> kakaoCallback(
            @RequestParam("code") String code,
            @RequestParam(value = "state", required = false) String state) {

        // 1. state 검증
        boolean isValidState = kakaoAuthService.validateState(state);
        if (!isValidState) {
            throw new BusinessException(BusinessError.KAKAO_LOGIN_ERROR);
        }

        // 2. 받은 인가 코드(code)를 이용해 토큰 요청을 진행할 수 있도록 다음 처리 호출
        KakoAuthTokenResponse kakoAuthTokenResponse = kakaoAuthService.requestAccessToken(code);
        KakaoUserResponse kakaoUserResponse = kakaoAuthService.requestUserInfo(kakoAuthTokenResponse.getAccess_token());

        // 3. 유저정보 DB에 저장하고, 사용자에게 accessToken, refreshToken 전달
        TokenResponse tokenResponse = kakaoAuthService.saveOrUpdateUser(kakaoUserResponse);
        return ResponseEntity.ok(tokenResponse);
    }
}
