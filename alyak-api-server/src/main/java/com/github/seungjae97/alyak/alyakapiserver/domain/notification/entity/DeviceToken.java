package com.github.seungjae97.alyak.alyakapiserver.domain.notification.entity;

import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "device_token",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_device_token_user_device", columnNames = {"user_id", "device_id"})
        }
)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeviceToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_token_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "device_id", nullable = false, length = 100)
    private String deviceId;

    @Column(name = "fcm_token", nullable = false, length = 512)
    private String fcmToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform", nullable = false, length = 20)
    private DevicePlatform platform;

    @Column(name = "app_version", length = 50)
    private String appVersion;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "last_seen_at", nullable = false)
    private LocalDateTime lastSeenAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void updateToken(String fcmToken, DevicePlatform platform, String appVersion, LocalDateTime now) {
        this.fcmToken = fcmToken;
        this.platform = platform;
        this.appVersion = appVersion;
        this.enabled = true;
        this.lastSeenAt = now;
        this.updatedAt = now;
    }

    public void disable(LocalDateTime now) {
        this.enabled = false;
        this.updatedAt = now;
    }
}
