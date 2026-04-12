package com.github.seungjae97.alyak.alyakapiserver.domain.notification.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.notification.entity.DeviceToken;

import java.util.List;

public interface PushNotificationService {
    void sendInvite(List<DeviceToken> deviceTokens, Long inviterUserId, String inviterName);

    /**
     * 복약 기록이 저장된 뒤, 같은 가족(또는 본인만)에게 데이터 메시지로 알립니다.
     *
     * @param medicationStatus {@code TAKEN}, {@code DELAYED}, {@code SKIPPED}
     */
    void sendMedicationLogNotification(
            List<DeviceToken> deviceTokens,
            Long actorUserId,
            String actorName,
            String pillName,
            String medicationStatus
    );
}
