package com.github.seungjae97.alyak.alyakapiserver.global.redis.controller;

import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.UserRepository;
import com.github.seungjae97.alyak.alyakapiserver.global.common.exception.BusinessError;
import com.github.seungjae97.alyak.alyakapiserver.global.common.exception.BusinessException;
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
    private final UserRepository userRepository;

    @PostMapping("/send")
    @Operation(summary = "이메일 전송" , description = "입력한 이메일로 6자리의 인증코드를 전송한다. 이미 가입된 이메일이면 전송하지 않는다.")
    public ResponseEntity<Void> sendEmail(@RequestParam String email) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(BusinessError.EMAIL_ALREADY_EXISTS);
        }
        mailService.sendAuthEmail(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify")
    @Operation(summary = "이메일 인증번호 검증", description = "성공 시 200, 실패 시 BusinessError(title)로 사유를 구분합니다.")
    public ResponseEntity<Void> verifyEmailCode(@RequestBody EmailValidationRequest emailValidationRequest) {
        mailService.verifyEmailAuthCode(
                emailValidationRequest.getEmail(),
                emailValidationRequest.getCode());
        return ResponseEntity.ok().build();
    }
}
