package com.github.seungjae97.alyak.alyakapiserver.global.auth.dto.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String email;
    private String password;
}
