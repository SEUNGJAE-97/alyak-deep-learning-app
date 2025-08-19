package com.github.seungjae97.alyak.alyakapiserver.global.Redis.Dto.Request;


import lombok.Data;

@Data
public class EmailValidationRequest {
    private String email;
    private String code;
}
