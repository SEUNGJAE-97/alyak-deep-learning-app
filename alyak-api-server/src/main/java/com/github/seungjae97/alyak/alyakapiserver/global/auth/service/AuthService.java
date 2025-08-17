package com.github.seungjae97.alyak.alyakapiserver.global.auth.service;

import com.github.seungjae97.alyak.alyakapiserver.global.auth.dto.Request.LoginRequest;
import com.github.seungjae97.alyak.alyakapiserver.global.auth.dto.Response.LoginResponse;
import com.github.seungjae97.alyak.alyakapiserver.global.auth.dto.Request.SignupRequest;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
    void signup(SignupRequest signupRequest);
    void logout(String token);
}
