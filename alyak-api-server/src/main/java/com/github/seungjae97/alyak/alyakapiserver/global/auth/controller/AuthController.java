package com.github.seungjae97.alyak.alyakapiserver.global.auth.controller;

import com.github.seungjae97.alyak.alyakapiserver.global.auth.dto.Request.LoginRequest;
import com.github.seungjae97.alyak.alyakapiserver.global.auth.dto.Response.LoginResponse;
import com.github.seungjae97.alyak.alyakapiserver.global.auth.dto.Request.SignupRequest;
import com.github.seungjae97.alyak.alyakapiserver.global.auth.dto.Response.TokenResponse;
import com.github.seungjae97.alyak.alyakapiserver.global.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "유저", description = "인증 관련 API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다.")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    public ResponseEntity<Void> signup(@RequestBody SignupRequest signupRequest) {
        authService.signup(signupRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "사용자를 로그아웃 처리합니다.")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/temp-login")
    public ResponseEntity<TokenResponse> tempLogin() {
        try {
            TokenResponse token = authService.tempLogin();
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
