package com.github.seungjae97.alyak.alyakapiserver.global.common.exception;

import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ErrorResponse businessExceptionHandler(BusinessException exception) {
        BusinessError businessError = exception.getBusinessError();
        exception.printStackTrace();
        return ErrorResponse
                .builder(exception, businessError.getHttpStatus(), businessError.getMessage())
                .title(businessError.name())
                .build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ErrorResponse businessExceptionHandler(RuntimeException exception) {
        exception.printStackTrace();
        return ErrorResponse
                .builder(exception, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류")
                .title("서버 내부 오류")
                .build();
    }

}
