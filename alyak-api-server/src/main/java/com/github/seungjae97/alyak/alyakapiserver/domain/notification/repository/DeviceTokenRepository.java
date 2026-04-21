package com.github.seungjae97.alyak.alyakapiserver.domain.notification.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.notification.entity.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    Optional<DeviceToken> findByUser_UserIdAndDeviceId(Long userId, String deviceId);
    List<DeviceToken> findAllByUser_UserIdAndEnabledTrue(Long userId);
    List<DeviceToken> findAllByFcmToken(String fcmToken);
}
