package com.github.seungjae97.alyak.alyakapiserver.global.redis.service;

import com.github.seungjae97.alyak.alyakapiserver.global.redis.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final JavaMailSender mailSender;
    private final RedisUtil redisUtil;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public String createAuthCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int idx = (int) (Math.random() * chars.length());
            builder.append(chars.charAt(idx));
        }
        return builder.toString();
    }

    public void sendAuthEmail(String toEmail) {
        String code = createAuthCode();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setFrom(senderEmail);
        message.setSubject("이메일 인증번호 안내");
        message.setText("인증번호는 " + code + " 입니다.");
        mailSender.send(message);
        redisUtil.setDataExpire("auth_request:" + toEmail, "requested", 120);
        redisUtil.setDataExpire(toEmail, code, 30);
    }

    public boolean verifyAuthCode(String email, String code) {
        String savedCode = redisUtil.getData(email);
        if (savedCode == null) return false;

        boolean matched = savedCode.equals(code);
        if (matched) {
            redisUtil.deleteData(email);
            redisUtil.setDataExpire("verified:" + email, "verified", 60);
        }
        return matched;
    }

    public String createToken(String email) {
        String code = createAuthCode();
        redisUtil.setDataExpire("token:" + email, code, 60);
        return code;
    }
}
