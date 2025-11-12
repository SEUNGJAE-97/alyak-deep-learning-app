package com.github.seungjae97.alyak.alyakapiserver.global.common.exception;

import org.springframework.web.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BusinessException.class)
    public ErrorResponse businessExceptionHandler(BusinessException exception) {
        BusinessError businessError = exception.getBusinessError();
        exception.printStackTrace();
        return ErrorResponse
                .builder(exception, businessError.getHttpStatus(), businessError.getMessage())
                .title(businessError.name())
                .build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ErrorResponse businessExceptionHandler(RuntimeException exception) {
        exception.printStackTrace();
        return ErrorResponse
                .builder(exception, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류")
                .title("서버 내부 오류")
                .build();
    }

}
