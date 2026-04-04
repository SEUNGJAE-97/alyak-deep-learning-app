package com.github.seungjae97.alyak.alyakapiserver.global.redis.controller;

import com.github.seungjae97.alyak.alyakapiserver.global.redis.dto.request.EmailValidationRequest;
import com.github.seungjae97.alyak.alyakapiserver.global.redis.service.RedisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@Tag(name = "04. 이메일 인증", description = "이메일 인증 관련 API")
public class EmailController {
    private final RedisService mailService;

    @PostMapping("/send")
    @Operation(summary = "이메일 전송" , description = "입력한 이메일로 6자리의 인증코드를 전송한다.")
    public ResponseEntity<Void> sendEmail(@RequestParam String email) {
        mailService.sendAuthEmail(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify")
    public ResponseEntity<Boolean> verifyEmailCode(@RequestBody EmailValidationRequest emailValidationRequest) {
        String email = emailValidationRequest.getEmail();
        String code = emailValidationRequest.getCode();
        return ResponseEntity.ok(mailService.verifyAuthCode(email, code));
    }
}
