package com.github.seungjae97.alyak.alyakapiserver.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.Provider;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.ProviderId;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.ProviderRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import support.RepositoryTestBase;
import support.RepositoryTestFixtures;

import static org.assertj.core.api.Assertions.assertThat;

class ProviderRepositoryTest extends RepositoryTestBase {

    @Autowired private ProviderRepository providerRepository;
    @Autowired private UserRepository userRepository;

    private Long userId;
    private ProviderId providerId;

    @BeforeEach
    void setUp() {
        User user = userRepository.save(RepositoryTestFixtures.user("provider"));
        userId = user.getUserId();
        providerId = new ProviderId("KAKAO", userId);

        providerRepository.save(Provider.builder()
                .id(providerId)
                .user(user)
                .build());
        flushAndClear();
    }

    @Test
    @DisplayName("저장 후 복합키로 조회 시 데이터가 일치한다")
    void save_and_findById() {
        Provider found = providerRepository.findById(providerId).orElseThrow();
        assertThat(found.getUser().getUserId()).isEqualTo(userId);
        assertThat(found.getId().getProviderName()).isEqualTo("KAKAO");
    }

    @Test
    @DisplayName("존재하지 않는 복합키 조회 시 empty를 반환한다")
    void findById_notFound() {
        assertNotFound(providerRepository.findById(new ProviderId("GOOGLE", 999_999L)));
    }

    @Test
    @DisplayName("deleteByUser_UserId로 Provider를 일괄 삭제한다")
    void deleteByUser_UserId() {
        providerRepository.deleteByUser_UserId(userId);
        flushAndClear();

        assertThat(providerRepository.findById(providerId)).isEmpty();
    }

    @Test
    @DisplayName("사용자 삭제 시 CASCADE로 Provider가 삭제된다")
    void deleteUser_cascadesToProvider() {
        userRepository.deleteById(userId);
        flushAndClear();

        assertThat(providerRepository.findById(providerId)).isEmpty();
    }
}
