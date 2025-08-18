package com.github.seungjae97.alyak.alyakapiserver.global.Redis.Controller;

import com.github.seungjae97.alyak.alyakapiserver.global.Redis.Service.RedisService;
import com.github.seungjae97.alyak.alyakapiserver.global.Redis.Util.RedisUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {
    private final RedisService mailService;
    private final RedisUtil redisUtil;

    @PostMapping("/send")
    @Operation(summary = "이메일 전송" , description = "입력한 이메일로 6자리의 인증코드를 전송한다.")
    public ResponseEntity<Void> sendEmail(@RequestBody String email) {
        mailService.sendAuthEmail(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify")
    public ResponseEntity<Boolean> verifyEmailCode(@RequestBody Map<String, String> params) {
        String email = params.get("email");
        String code = params.get("code");
        boolean result = mailService.verifyAuthCode(email, code);
        if (result) {
            redisUtil.deleteData(email);
        }
        return ResponseEntity.ok(result);
    }
}
