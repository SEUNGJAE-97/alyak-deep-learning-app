package com.github.seungjae97.alyak.alyakapiserver.domain.auth.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Request.LoginRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response.LoginResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Request.SignupRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response.TokenResponse;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
    TokenResponse signup(SignupRequest signupRequest);
    void logout(String token);
    TokenResponse tempLogin();
    TokenResponse reissue(String refreshToken);
}
