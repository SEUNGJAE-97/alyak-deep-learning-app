package com.github.seungjae97.alyak.alyakapiserver.domain.notification.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.notification.entity.DeviceToken;

import java.util.List;

public interface PushNotificationService {
    void sendInvite(List<DeviceToken> deviceTokens, Long inviterUserId, String inviterName);
}
