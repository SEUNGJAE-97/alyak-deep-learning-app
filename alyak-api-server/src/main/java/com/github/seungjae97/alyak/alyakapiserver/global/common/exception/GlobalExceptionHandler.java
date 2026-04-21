package com.github.seungjae97.alyak.alyakapiserver.global.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ProblemDetail> businessExceptionHandler(BusinessException exception) {
        BusinessError businessError = exception.getBusinessError();
        exception.printStackTrace();
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                businessError.getHttpStatus(),
                businessError.getMessage());
        problemDetail.setTitle(businessError.name());
        return ResponseEntity.status(businessError.getHttpStatus()).body(problemDetail);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail runtimeExceptionHandler(RuntimeException exception) {
        exception.printStackTrace();
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "서버 내부 오류");
        problemDetail.setTitle("서버 내부 오류");
        return problemDetail;
    }

}
