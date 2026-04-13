package com.github.seungjae97.alyak.alyakapiserver.domain.notification.dto.request;

import com.github.seungjae97.alyak.alyakapiserver.domain.notification.entity.DevicePlatform;
import lombok.Data;

@Data
public class UpsertDeviceTokenRequest {
    private String deviceId;
    private String fcmToken;
    private DevicePlatform platform;
    private String appVersion;
}
