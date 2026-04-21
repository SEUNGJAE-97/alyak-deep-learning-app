package com.github.seungjae97.alyak.alyakapiserver.global.mail.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    /**
     * 가족 초대 이메일을 발송한다.
     * 가입된 사용자라면 앱 내 초대 확인 안내를, 미가입 사용자라면 앱 설치 유도 문구를 보낸다.
     */
    public void sendFamilyInviteEmail(String toEmail, String inviterName, boolean alreadySignedUp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setFrom(senderEmail);
        message.setSubject("ALYAK 가족 초대 안내");

        if (alreadySignedUp) {
            message.setText(inviterName + "님이 가족 구성원으로 초대했습니다.\n앱에서 초대 내역을 확인해 주세요.");
        } else {
            message.setText(inviterName + "님이 ALYAK 가족 구성원으로 초대했습니다.\n앱을 설치하고 가입 후 초대를 확인해 주세요.");
        }

        mailSender.send(message);
    }
}
