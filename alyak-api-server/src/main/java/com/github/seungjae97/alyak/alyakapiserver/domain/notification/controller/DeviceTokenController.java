package com.github.seungjae97.alyak.alyakapiserver.domain.notification.controller;

import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.UserDetailsImpl;
import com.github.seungjae97.alyak.alyakapiserver.domain.notification.dto.request.DeleteDeviceTokenRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.notification.dto.request.UpsertDeviceTokenRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.notification.service.DeviceTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "07. 알림", description = "푸시 알림 토큰 관련 API")
public class DeviceTokenController {

    private final DeviceTokenService deviceTokenService;

    @PutMapping("/device-token")
    @Operation(summary = "FCM 토큰 등록/갱신", description = "앱 실행 시 디바이스 토큰을 등록하거나 갱신한다.")
    public ResponseEntity<Void> upsertDeviceToken(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody UpsertDeviceTokenRequest request
    ) {
        deviceTokenService.upsertToken(userDetails.getUser().getUserId(), request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/device-token")
    @Operation(summary = "FCM 토큰 비활성화", description = "로그아웃 또는 토큰 폐기 시 디바이스 토큰을 비활성화한다.")
    public ResponseEntity<Void> disableDeviceToken(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody DeleteDeviceTokenRequest request
    ) {
        deviceTokenService.disableToken(userDetails.getUser().getUserId(), request.getDeviceId());
        return ResponseEntity.noContent().build();
    }
}
