package com.github.seungjae97.alyak.alyakapiserver.global.auth.dto.Response;

import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private Long userId;
    private User.Role role;
}
