package com.github.seungjae97.alyak.alyakapiserver.global.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final BusinessError businessError;

    public BusinessException(BusinessError businessError) {
        super(businessError.getMessage());
        this.businessError = businessError;
    }

    public BusinessException(BusinessError businessError, Object... args) {
        super(String.format(businessError.getMessage(), args));
        this.businessError = businessError;
    }
}
