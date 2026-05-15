package com.github.seungjae97.alyak.alyakapiserver.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.notification.entity.DeviceToken;
import com.github.seungjae97.alyak.alyakapiserver.domain.notification.repository.DeviceTokenRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import support.QueryCounter;
import support.RepositoryTestBase;
import support.RepositoryTestFixtures;

import static org.assertj.core.api.Assertions.assertThat;

class DeviceTokenRepositoryTest extends RepositoryTestBase {

    @Autowired private DeviceTokenRepository deviceTokenRepository;
    @Autowired private UserRepository userRepository;

    private Long userId;
    private Long tokenId;
    private String deviceId;
    private String fcmToken;

    @BeforeEach
    void setUp() {
        User user = userRepository.save(RepositoryTestFixtures.user("device"));
        userId = user.getUserId();
        deviceId = "device-1";
        fcmToken = "fcm-token-1";

        DeviceToken saved = deviceTokenRepository.save(
                RepositoryTestFixtures.deviceToken(user, deviceId, fcmToken));
        tokenId = saved.getId();
        flushAndClear();
    }

    @Test
    @DisplayName("저장 후 ID가 생성되고 조회 시 데이터가 일치한다")
    void save_and_findById() {
        DeviceToken found = deviceTokenRepository.findById(tokenId).orElseThrow();
        assertThat(found.getDeviceId()).isEqualTo(deviceId);
        assertThat(found.getFcmToken()).isEqualTo(fcmToken);
    }

    @Test
    @DisplayName("존재하지 않는 ID 조회 시 empty를 반환한다")
    void findById_notFound() {
        assertNotFound(deviceTokenRepository.findById(999_999L));
    }

    @Test
    @DisplayName("userId와 deviceId로 토큰을 조회한다")
    void findByUser_UserIdAndDeviceId() {
        assertThat(deviceTokenRepository.findByUser_UserIdAndDeviceId(userId, deviceId))
                .isPresent();
    }

    @Test
    @DisplayName("활성화된 사용자 토큰 목록을 조회한다")
    void findAllByUser_UserIdAndEnabledTrue() {
        assertThat(deviceTokenRepository.findAllByUser_UserIdAndEnabledTrue(userId)).hasSize(1);
    }

    @Test
    @DisplayName("FCM 토큰으로 목록을 조회한다")
    void findAllByFcmToken() {
        assertThat(deviceTokenRepository.findAllByFcmToken(fcmToken)).hasSize(1);
    }

    @Test
    @DisplayName("findAllByUser_UserIdAndEnabledTrue 후 User 접근 시 N+1이 발생할 수 있다")
    void findAllByUser_accessingUser_mayCauseNPlusOne() {
        var tokens = deviceTokenRepository.findAllByUser_UserIdAndEnabledTrue(userId);
        tokens.forEach(t -> t.getUser().getName());

        assertThat(QueryCounter.getCount()).isGreaterThan(1);
    }

    @Test
    @DisplayName("findById는 단일 쿼리로 조회한다")
    void findById_singleQuery() {
        deviceTokenRepository.findById(tokenId);
        assertQueryCount(1);
    }

    @Test
    @DisplayName("토큰 삭제 후 조회되지 않는다")
    void deleteToken() {
        deviceTokenRepository.deleteById(tokenId);
        flushAndClear();

        assertThat(deviceTokenRepository.findById(tokenId)).isEmpty();
    }
}
