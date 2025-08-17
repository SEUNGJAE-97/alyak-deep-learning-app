package com.github.seungjae97.alyak.alyakapiserver.global.Redis.Service;

import com.github.seungjae97.alyak.alyakapiserver.global.Redis.Util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final JavaMailSender mailSender;
    private final RedisUtil redisUtil;

    private static final String SENDER_ADDRESS = "your@email.com";

    // 1. 랜덤 인증코드 생성
    public String createAuthCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int idx = (int) (Math.random() * chars.length());
            builder.append(chars.charAt(idx));
        }
        return builder.toString();
    }

    //2. 인증 메일 전송, Redis 에 저장
    public void sendAuthEmail(String toEmail) {
        String code = createAuthCode();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setFrom(SENDER_ADDRESS);
        message.setSubject("이메일 인증번호 안내");
        message.setText("인증번호는 " + code + " 입니다.");
        mailSender.send(message);
        redisUtil.setDataExpire(toEmail, code, 300);
    }
    //3. 인증 번호 검증
    public boolean verifyAuthCode(String email, String code) {
        String savedCode = redisUtil.getData(email);
        return savedCode != null && savedCode.equals(code);
    }
}
