package com.github.seungjae97.alyak.alyakapiserver.global.redis.dto.request;

import lombok.Data;

@Data
public class EmailValidationRequest {
    private String email;
    private String code;
}
