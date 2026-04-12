package com.github.seungjae97.alyak.alyakapiserver.domain.medication.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.family.entity.Family;
import com.github.seungjae97.alyak.alyakapiserver.domain.medication.dto.request.MedicationLogRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.medication.entity.MedicationLog;
import com.github.seungjae97.alyak.alyakapiserver.domain.medication.enums.MedicationStatus;
import com.github.seungjae97.alyak.alyakapiserver.domain.medication.repository.MedicationLogRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.notification.entity.DeviceToken;
import com.github.seungjae97.alyak.alyakapiserver.domain.notification.repository.DeviceTokenRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.notification.service.PushNotificationService;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicationLogService {

    private final MedicationLogRepository medicationLogRepository;
    private final UserRepository userRepository;
    private final DeviceTokenRepository deviceTokenRepository;
    private final PushNotificationService pushNotificationService;

    @Transactional
    public void log(Long userId, MedicationLogRequest request) {
        validateRequest(request);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        MedicationStatus status = resolveStatus(request);

        MedicationLog medicationLog = MedicationLog.builder()
                .user(user)
                .pillName(request.getPillName().trim())
                .dosage(request.getDosage())
                .scheduledTime(request.getScheduledTime())
                .takenTime(request.getTakenTime())
                .status(status)
                .build();

        medicationLogRepository.save(medicationLog);

        try {
            notifyFamilyMedicationPush(user, request, status);
        } catch (Exception e) {
            log.warn("가족 복약 FCM 전송 실패 (medication_log는 저장됨): {}", e.getMessage(), e);
        }
    }

    /**
     * 같은 가족에 속한 모든 사용자(본인 포함)의 활성 기기로 알림을 보냅니다. 가족이 없으면 본인 기기만 대상입니다.
     * FCM 토큰은 중복 제거합니다.
     */
    private void notifyFamilyMedicationPush(User actor, MedicationLogRequest request, MedicationStatus status) {
        List<DeviceToken> tokens = collectFamilyDeviceTokens(actor);
        if (tokens.isEmpty()) {
            return;
        }
        pushNotificationService.sendMedicationLogNotification(
                tokens,
                actor.getUserId(),
                actor.getName(),
                request.getPillName().trim(),
                status.name()
        );
    }

    private List<DeviceToken> collectFamilyDeviceTokens(User actor) {
        Set<Long> userIds = new LinkedHashSet<>();
        Family family = actor.getFamily();
        if (family != null && family.getUsers() != null) {
            for (User u : family.getUsers()) {
                userIds.add(u.getUserId());
            }
        } else {
            userIds.add(actor.getUserId());
        }

        Map<String, DeviceToken> byFcmToken = new LinkedHashMap<>();
        for (Long uid : userIds) {
            for (DeviceToken dt : deviceTokenRepository.findAllByUser_UserIdAndEnabledTrue(uid)) {
                byFcmToken.putIfAbsent(dt.getFcmToken(), dt);
            }
        }
        return new ArrayList<>(byFcmToken.values());
    }

    /**
     * 본인이거나 같은 가족 구성원의 복용 통계만 조회할 수 있습니다.
     */
    public void assertCanViewMedicationStats(Long viewerUserId, Long subjectUserId) {
        if (Objects.equals(viewerUserId, subjectUserId)) {
            return;
        }
        User viewer = userRepository.findById(viewerUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        User subject = userRepository.findById(subjectUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "대상 사용자를 찾을 수 없습니다."));

        if (viewer.getFamily() == null || subject.getFamily() == null
                || !Objects.equals(viewer.getFamily().getId(), subject.getFamily().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "같은 가족 구성원만 조회할 수 있습니다.");
        }
    }

    private static MedicationStatus resolveStatus(MedicationLogRequest request) {
        if (request.getTakenTime() == null) {
            return MedicationStatus.SKIPPED;
        }
        if (request.getTakenTime().isAfter(request.getScheduledTime().plusMinutes(30))) {
            return MedicationStatus.DELAYED;
        }
        return MedicationStatus.TAKEN;
    }

    private static void validateRequest(MedicationLogRequest request) {
        if (!StringUtils.hasText(request.getPillName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "pillName은 필수입니다.");
        }
        if (request.getDosage() == null || request.getDosage() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dosage는 1 이상이어야 합니다.");
        }
        if (request.getScheduledTime() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "scheduledTime은 필수입니다.");
        }
    }
}
