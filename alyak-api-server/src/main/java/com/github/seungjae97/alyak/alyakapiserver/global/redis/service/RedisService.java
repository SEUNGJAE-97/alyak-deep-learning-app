package com.github.seungjae97.alyak.alyakapiserver.global.redis.service;

import com.github.seungjae97.alyak.alyakapiserver.global.common.exception.BusinessError;
import com.github.seungjae97.alyak.alyakapiserver.global.common.exception.BusinessException;
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

    /** 인증코드·인증 완료(verified) 공통 TTL — 5분 */
    private static final long EMAIL_AUTH_FIVE_MINUTES_SECONDS = 300L;
    /**
     * 인증 메일 발송 이력. 코드 TTL보다 길게 두어 만료/미요청을 구분한다.
     */
    private static final long EMAIL_AUTH_REQUEST_TTL_SECONDS = 420L;

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
        redisUtil.setDataExpire("auth_request:" + toEmail, "requested", EMAIL_AUTH_REQUEST_TTL_SECONDS);
        redisUtil.setDataExpire(toEmail, code, EMAIL_AUTH_FIVE_MINUTES_SECONDS);
    }

    /** 인증 성공 시 `verified:` 키를 설정합니다. 실패 시 {@link BusinessException}. */
    public void verifyEmailAuthCode(String email, String code) {
        String savedCode = redisUtil.getData(email);
        if (savedCode == null) {
            if (redisUtil.existData("auth_request:" + email)) {
                throw new BusinessException(BusinessError.EMAIL_VERIFICATION_EXPIRED);
            }
            throw new BusinessException(BusinessError.EMAIL_VERIFICATION_REQUEST_NEEDED);
        }
        if (!savedCode.equals(code)) {
            throw new BusinessException(BusinessError.EMAIL_CODE_MISMATCH);
        }
        redisUtil.deleteData(email);
        redisUtil.setDataExpire("verified:" + email, "verified", EMAIL_AUTH_FIVE_MINUTES_SECONDS);
    }

    /**
     * @param email qr을 생성하는 사람의 이메일
     * @return code 랜덤 생성된 임시 코드값
     * */
    public String createToken(String email) {
        String code = createAuthCode();
        redisUtil.setDataExpire("token:" + email, code, 60);
        redisUtil.setDataExpire("qr_code:" + code, email, 60);
        return code;
    }

    public String getInviterEmailByQrCode(String scannedCode) {
        if (scannedCode == null || scannedCode.isBlank()) {
            return null;
        }
        return redisUtil.getData("qr_code:" + scannedCode.trim());
    }

    /** QR 일회용 처리: 역방향·정방향 키 제거 */
    public void deleteFamilyQrMappings(String scannedCode, String inviterEmail) {
        if (scannedCode != null && !scannedCode.isBlank()) {
            redisUtil.deleteData("qr_code:" + scannedCode.trim());
        }
        if (inviterEmail != null && !inviterEmail.isBlank()) {
            redisUtil.deleteData("token:" + inviterEmail.trim());
        }
    }

    private static final long FAMILY_INVITE_PENDING_TTL_SECONDS = 86_400L; // 24시간

    /**
     * 가족 초대가 발송된 뒤 수락 API에서 검증할 수 있도록 Redis에 보관합니다.
     */
    public void saveFamilyInvitePending(Long inviteeUserId, Long inviterUserId) {
        String key = "familyInvite:pending:" + inviteeUserId + ":" + inviterUserId;
        redisUtil.setDataExpire(key, "1", FAMILY_INVITE_PENDING_TTL_SECONDS);
    }

    /**
     * 초대 수락 확정 시 키를 삭제합니다.
     *
     * @return 삭제 전에 보류 초대가 있었으면 true
     */
    public boolean verifyAndConsumeFamilyInvitePending(Long inviteeUserId, Long inviterUserId) {
        String key = "familyInvite:pending:" + inviteeUserId + ":" + inviterUserId;
        String val = redisUtil.getData(key);
        if (!"1".equals(val)) {
            return false;
        }
        redisUtil.deleteData(key);
        return true;
    }
}
