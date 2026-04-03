package com.github.seungjae97.alyak.alyakapiserver.domain.family.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.family.dto.response.FamilyMemberInfoResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.family.entity.Family;
import com.github.seungjae97.alyak.alyakapiserver.domain.family.repository.FamilyRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.notification.entity.DeviceToken;
import com.github.seungjae97.alyak.alyakapiserver.domain.notification.repository.DeviceTokenRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.notification.service.PushNotificationService;
import com.github.seungjae97.alyak.alyakapiserver.domain.schedule.service.MedicationStatsService;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.UserRepository;
import com.github.seungjae97.alyak.alyakapiserver.global.redis.service.RedisService;
import com.github.seungjae97.alyak.alyakapiserver.global.common.exception.BusinessError;
import com.github.seungjae97.alyak.alyakapiserver.global.common.exception.BusinessException;
import com.github.seungjae97.alyak.alyakapiserver.global.mail.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FamilyService {

    private final FamilyRepository familyRepository;
    private final UserRepository userRepository;
    private final MedicationStatsService medicationStatsService;
    private final RedisService redisService;
    private final EmailService emailService;
    private final DeviceTokenRepository deviceTokenRepository;
    private final PushNotificationService pushNotificationService;
    /**
     * @param userId 유저 아이디
     * @return List<FamilyMemberInfoResponse> members 가족에 속하는 구성원들의 정보
     * */
    public List<FamilyMemberInfoResponse> findMembersByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(BusinessError.USER_NOT_EXIST));

        Family family = user.getFamily();

        if (family == null) {
            FamilyMemberInfoResponse selfResponse = FamilyMemberInfoResponse.from(user);
            selfResponse.setRole("본인");

            selfResponse.setStats(medicationStatsService.calculateMemberStats(userId));
            selfResponse.setWeeklyMedicationStats(medicationStatsService.calculateWeeklyStats(userId));

            return List.of(selfResponse); // 본인만 포함된 리스트 반환
        }

        return family.getUsers().stream()
                .map(member -> {
                    // 기본 정보 생성
                    FamilyMemberInfoResponse response = FamilyMemberInfoResponse.from(member);
                    
                    // 본인 여부에 따라 role 설정
                    if (member.getUserId().equals(user.getUserId())) {
                        response.setRole("본인");
                    } else {
                        response.setRole("가족 구성원");  // 추후 관계 테이블 추가 시 수정
                    }
                    
                    // 통계 정보 계산 및 설정
                    response.setStats(medicationStatsService.calculateMemberStats(member.getUserId()));
                    response.setWeeklyMedicationStats(
                        medicationStatsService.calculateWeeklyStats(member.getUserId())
                    );
                    
                    return response;
                })
                .toList();
    }

    /**
     * @param userId 유저 아이디
     * @return qrcode userId - 생성된 임시 코드 구조로 redis에 저장한다,
     * */
    public String getQrCode(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(BusinessError.USER_NOT_EXIST));
        return redisService.createToken(user.getEmail());
    }

    /**
     * 대상 이메일로 가족 초대 요청을 전송한다.
     * 가입 여부를 확인한 뒤 초대 안내 메일을 발송한다.
     *
     * @param email 초대할 대상 이메일
     * @param userId 초대를 요청한 사용자 ID
     * @return 초대 요청 처리 성공 여부
     */
    public boolean inviteByEmail(String email, Long userId) {
        User inviter = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(BusinessError.USER_NOT_EXIST));

        userRepository.findByEmail(email).ifPresentOrElse(
                targetUser -> {
                    redisService.saveFamilyInvitePending(targetUser.getUserId(), inviter.getUserId());
                    List<DeviceToken> activeDeviceTokens = deviceTokenRepository
                            .findAllByUser_UserIdAndEnabledTrue(targetUser.getUserId());
                    if (activeDeviceTokens.isEmpty()) {
                        emailService.sendFamilyInviteEmail(email, inviter.getName(), true);
                        return;
                    }
                    pushNotificationService.sendInvite(activeDeviceTokens, inviter.getUserId(), inviter.getName());
                },
                () -> emailService.sendFamilyInviteEmail(email, inviter.getName(), false)
        );

        return true;
    }

    /**
     * 초대받은 사용자가 로그인 상태에서 초대를 수락합니다.
     * Redis에 저장된 보류 초대가 있어야 하며, 1회 성공 시 키가 삭제됩니다.
     */
    @Transactional
    public void acceptInvite(Long inviteeUserId, Long inviterUserId) {
        if (inviteeUserId.equals(inviterUserId)) {
            throw new BusinessException(BusinessError.INVITE_SELF_NOT_ALLOWED);
        }

        User invitee = userRepository.findById(inviteeUserId)
                .orElseThrow(() -> new BusinessException(BusinessError.USER_NOT_EXIST));
        User inviter = userRepository.findById(inviterUserId)
                .orElseThrow(() -> new BusinessException(BusinessError.USER_NOT_EXIST));

        if (invitee.getFamily() != null && inviter.getFamily() != null
                && invitee.getFamily().getId().equals(inviter.getFamily().getId())) {
            redisService.verifyAndConsumeFamilyInvitePending(inviteeUserId, inviterUserId);
            return;
        }

        if (invitee.getFamily() != null) {
            throw new BusinessException(BusinessError.ALREADY_IN_OTHER_FAMILY);
        }

        if (!redisService.verifyAndConsumeFamilyInvitePending(inviteeUserId, inviterUserId)) {
            throw new BusinessException(BusinessError.FAMILY_INVITE_EXPIRED_OR_INVALID);
        }

        Family targetFamily = inviter.getFamily();
        if (targetFamily == null) {
            targetFamily = familyRepository.save(Family.builder().build());
            inviter.assignFamily(targetFamily);
            userRepository.save(inviter);
        }

        invitee.assignFamily(targetFamily);
        userRepository.save(invitee);
    }
}
