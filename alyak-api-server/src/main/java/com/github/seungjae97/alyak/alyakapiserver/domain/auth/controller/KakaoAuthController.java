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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
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
    public KakaoAuthCodeResponse getAuthorizationUrl(HttpServletRequest request) {
        String host = request.getHeader("Host");
        String currentBaseUrl = "http://" + host;
        String state = UUID.randomUUID().toString();
        String authorizationUrl = kakaoAuthService.buildAuthorizationUrl(state, currentBaseUrl);

        return new KakaoAuthCodeResponse(authorizationUrl);
    }

    @GetMapping("/callback")
    @Operation(summary = "카카오 로그인 콜백 API", description = "서버내부에서 호출하는 용도")
    public void kakaoCallback(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam("code") String code,
            @RequestParam(value = "state", required = false) String state) throws IOException{

        String host = request.getHeader("Host");
        String currentBaseUrl = "http://" + host;
        String currentRedirectUri = currentBaseUrl + "/auth/kakao/callback";

        // 1. state 검증
        boolean isValidState = kakaoAuthService.validateState(state);
        if (!isValidState) {
            throw new BusinessException(BusinessError.KAKAO_LOGIN_ERROR);
        }

        // 2. 받은 인가 코드(code)를 이용해 토큰 요청을 진행할 수 있도록 다음 처리 호출
        KakoAuthTokenResponse kakoAuthTokenResponse = kakaoAuthService.requestAccessToken(code, currentRedirectUri);
        KakaoUserResponse kakaoUserResponse = kakaoAuthService.requestUserInfo(kakoAuthTokenResponse.getAccess_token());

        // 3. 유저정보 DB에 저장하고, 사용자에게 accessToken, refreshToken 전달
        TokenResponse tokenResponse = kakaoAuthService.saveOrUpdateUser(kakaoUserResponse);

        //4. 앱의 딥링크 주소를 토큰에 담아서 리다이렉트
        String redirectUrl = UriComponentsBuilder.fromUriString("alyak://auth")
                .queryParam("accessToken", tokenResponse.getAccessToken())
                .queryParam("refreshToken", tokenResponse.getRefreshToken())
                .queryParam("email", tokenResponse.getEmail())
                .queryParam("userId", tokenResponse.getUserId())
                .queryParam("userName", tokenResponse.getUserName())
                .encode()
                .build().toUriString();

        response.sendRedirect(redirectUrl);
    }
}
