package com.github.seungjae97.alyak.alyakapiserver.domain.auth.controller;

import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Request.LoginRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Request.TokenRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response.LoginResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Request.PasswordResetRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Request.SignupRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response.TokenResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "01.Auth", description = "사용자 인증 관련 API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다.")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/login")
    @Operation(summary = "관리자 로그인", description = "이메일과 비밀번호로 로그인하며, 관리자 권한 사용자만 로그인할 수 있습니다.")
    public ResponseEntity<LoginResponse> adminLogin(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.adminLogin(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    public ResponseEntity<TokenResponse> signup(@RequestBody SignupRequest signupRequest) {
        TokenResponse response = authService.signup(signupRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/password/reset")
    @Operation(
            summary = "비밀번호 찾기(재설정)",
            description = "가입된 이메일로 인증번호를 받고(/api/email/send/reset → /api/email/verify) 인증 완료 후, JWT 없이 새 비밀번호를 설정합니다.")
    public ResponseEntity<Void> resetPassword(@RequestBody PasswordResetRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "사용자를 로그아웃 처리합니다.")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reissue")
    @Operation(summary = "토큰 재발급", description = "refresh token을 통해 access token 재발급")
    public ResponseEntity<TokenResponse> reissue(@RequestBody TokenRequest request) {
        TokenResponse token = authService.reissue(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }


    @PostMapping("/temp-login")
    @Operation(summary = "임시 로그인", description = "개발용 임시 로그인")
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
