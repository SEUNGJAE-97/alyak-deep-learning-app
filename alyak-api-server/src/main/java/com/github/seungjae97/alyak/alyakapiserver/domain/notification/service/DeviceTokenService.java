package com.github.seungjae97.alyak.alyakapiserver.domain.notification.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.notification.dto.request.UpsertDeviceTokenRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.notification.entity.DeviceToken;
import com.github.seungjae97.alyak.alyakapiserver.domain.notification.repository.DeviceTokenRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.UserRepository;
import com.github.seungjae97.alyak.alyakapiserver.global.common.exception.BusinessError;
import com.github.seungjae97.alyak.alyakapiserver.global.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class DeviceTokenService {

    private final DeviceTokenRepository deviceTokenRepository;
    private final UserRepository userRepository;

    public void upsertToken(Long userId, UpsertDeviceTokenRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(BusinessError.USER_NOT_EXIST));

        LocalDateTime now = LocalDateTime.now();

        deviceTokenRepository.findByUser_UserIdAndDeviceId(userId, request.getDeviceId())
                .ifPresentOrElse(
                        token -> token.updateToken(request.getFcmToken(), request.getPlatform(), request.getAppVersion(), now),
                        () -> {
                            DeviceToken newToken = DeviceToken.builder()
                                    .user(user)
                                    .deviceId(request.getDeviceId())
                                    .fcmToken(request.getFcmToken())
                                    .platform(request.getPlatform())
                                    .appVersion(request.getAppVersion())
                                    .enabled(true)
                                    .lastSeenAt(now)
                                    .createdAt(now)
                                    .updatedAt(now)
                                    .build();
                            deviceTokenRepository.save(newToken);
                        }
                );
    }

    public void disableToken(Long userId, String deviceId) {
        LocalDateTime now = LocalDateTime.now();
        deviceTokenRepository.findByUser_UserIdAndDeviceId(userId, deviceId)
                .ifPresent(token -> token.disable(now));
    }
}
