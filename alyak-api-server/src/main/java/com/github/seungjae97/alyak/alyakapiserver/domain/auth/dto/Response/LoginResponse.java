package com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private Long expiresIn;
    private Long userId;
}
