package com.github.seungjae97.alyak.alyakapiserver.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private int idx;
    private String email;
    private String password;
    private String name;
    private enum Gender { M, F };
    private String resident_registration_number;
    private String phone_number;
}
