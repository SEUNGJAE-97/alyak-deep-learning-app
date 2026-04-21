package com.github.seungjae97.alyak.alyakapiserver.domain.family.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.family.dto.response.FamilyMemberInfoResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.family.entity.Family;
import com.github.seungjae97.alyak.alyakapiserver.domain.family.entity.FamilyMember;
import com.github.seungjae97.alyak.alyakapiserver.domain.family.repository.FamilyMemberRepository;
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FamilyService {

    private final FamilyMemberRepository familyMemberRepository;
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

        List<FamilyMember> familyMemberList = user.getFamilyMembers();

        // 가족에 소속되지 않고 혼자인 경우
        if (familyMemberList.isEmpty()) {
            FamilyMemberInfoResponse selfResponse = FamilyMemberInfoResponse.from(user);
            selfResponse.setRole("본인");
            selfResponse.setStats(medicationStatsService.calculateMemberStats(userId));
            selfResponse.setWeeklyMedicationStats(medicationStatsService.calculateWeeklyStats(userId));
            return List.of(selfResponse);
        }

        Map<Long, User> memberMap = new LinkedHashMap<>();
        for (FamilyMember fm : familyMemberList) {
            List<FamilyMember> familyMembers = familyMemberRepository
                    .findByFamily_Id(fm.getFamily().getId());
            for (FamilyMember member : familyMembers) {
                memberMap.putIfAbsent(member.getUser().getUserId(), member.getUser());
            }
        }

        return memberMap.values().stream()
                .map(member -> {
                    FamilyMemberInfoResponse response = FamilyMemberInfoResponse.from(member);
                    response.setRole(member.getUserId().equals(userId) ? "본인" : "가족 구성원");
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

        boolean alreadySameFamily = invitee.getFamilyMembers().stream()
                .anyMatch(fm -> inviter.getFamilyMembers().stream()
                        .anyMatch(ifm -> ifm.getFamily().getId().equals(fm.getFamily().getId())));

        if (alreadySameFamily) {
            redisService.verifyAndConsumeFamilyInvitePending(inviteeUserId, inviterUserId);
            return;
        }

        if (!invitee.getFamilyMembers().isEmpty()) {
            throw new BusinessException(BusinessError.ALREADY_IN_OTHER_FAMILY);
        }

        if (!redisService.verifyAndConsumeFamilyInvitePending(inviteeUserId, inviterUserId)) {
            throw new BusinessException(BusinessError.FAMILY_INVITE_EXPIRED_OR_INVALID);
        }


        mergeInviteeIntoInvitersFamily(invitee, inviter, null);
    }

    /**
     * 스캔한 QR 코드(역방향 Redis)로 초대자를 특정한 뒤, 로그인 사용자를 그 가족에 합류시킵니다.
     * 성공·멱등(이미 동일 가족) 시 Redis QR 매핑을 삭제합니다.
     */
    @Transactional
    public void joinByQrCode(Long scannerUserId, String scannedCode) {
        String code = scannedCode == null ? "" : scannedCode.trim();
        if (code.isEmpty()) {
            throw new BusinessException(BusinessError.FAMILY_INVITE_EXPIRED_OR_INVALID);
        }

        String inviterEmail = redisService.getInviterEmailByQrCode(code);
        if (inviterEmail == null || inviterEmail.isBlank()) {
            throw new BusinessException(BusinessError.FAMILY_INVITE_EXPIRED_OR_INVALID);
        }
        String inviterEmailNorm = inviterEmail.trim();

        User inviter = userRepository.findByEmail(inviterEmailNorm)
                .orElseThrow(() -> new BusinessException(BusinessError.USER_NOT_EXIST));
        User invitee = userRepository.findById(scannerUserId)
                .orElseThrow(() -> new BusinessException(BusinessError.USER_NOT_EXIST));

        if (inviter.getUserId().equals(scannerUserId)) {
            throw new BusinessException(BusinessError.INVITE_SELF_NOT_ALLOWED);
        }

        boolean alreadySameFamily = invitee.getFamilyMembers().stream()
                .anyMatch(fm -> inviter.getFamilyMembers().stream()
                        .anyMatch(ifm -> ifm.getFamily().getId().equals(fm.getFamily().getId())));

        if (alreadySameFamily) {
            redisService.deleteFamilyQrMappings(code, inviterEmail.trim());
            return;
        }

        if (!invitee.getFamilyMembers().isEmpty()) {
            throw new BusinessException(BusinessError.ALREADY_IN_OTHER_FAMILY);
        }

        mergeInviteeIntoInvitersFamily(invitee, inviter, null);
        redisService.deleteFamilyQrMappings(code, inviterEmailNorm);
    }

    private void mergeInviteeIntoInvitersFamily(User invitee, User inviter, Long familyId) {
        Family targetFamily;

        if (inviter.getFamilyMembers().isEmpty()) {
            targetFamily = familyRepository.save(Family.builder().build());
            familyMemberRepository.save(FamilyMember.builder()
                    .user(inviter)
                    .family(targetFamily)
                    .build());
        } else if (familyId != null) {
            targetFamily = familyRepository.findById(familyId)
                    .orElseThrow(() -> new BusinessException(BusinessError.ALREADY_IN_OTHER_FAMILY));
        } else {
            targetFamily = inviter.getFamilyMembers().get(0).getFamily();
        }

        boolean alreadyJoined = familyMemberRepository
                .existsByUser_UserIdAndFamily_Id(invitee.getUserId(), targetFamily.getId());
        if (alreadyJoined) return;

        familyMemberRepository.save(FamilyMember.builder()
                .user(invitee)
                .family(targetFamily)
                .build());
    }
}
