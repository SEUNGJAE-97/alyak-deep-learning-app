package com.github.seungjae97.alyak.alyakapiserver.domain.notification.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.notification.entity.DeviceToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Profile("!prod")
public class NoopPushNotificationService implements PushNotificationService {
    @Override
    public void sendInvite(List<DeviceToken> deviceTokens, Long inviterUserId, String inviterName) {
        log.info("Push invite noop: inviterUserId={}, inviterName={}, targetDeviceCount={}",
                inviterUserId, inviterName, deviceTokens.size());
    }

    @Override
    public void sendMedicationLogNotification(
            List<DeviceToken> deviceTokens,
            Long actorUserId,
            String actorName,
            String pillName,
            String medicationStatus
    ) {
        log.info("Push medication log noop: actorUserId={}, pillName={}, status={}, targetDeviceCount={}",
                actorUserId, pillName, medicationStatus, deviceTokens.size());
    }
}
