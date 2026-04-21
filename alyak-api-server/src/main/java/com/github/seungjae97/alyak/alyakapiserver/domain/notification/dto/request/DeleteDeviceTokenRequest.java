package com.github.seungjae97.alyak.alyakapiserver.domain.notification.dto.request;

import lombok.Data;

@Data
public class DeleteDeviceTokenRequest {
    private String deviceId;
}
