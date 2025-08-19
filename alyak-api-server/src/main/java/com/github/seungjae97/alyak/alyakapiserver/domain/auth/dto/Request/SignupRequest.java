package com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    private String email;
    private String password;
    private String name;
    private String phoneNumber;
}
