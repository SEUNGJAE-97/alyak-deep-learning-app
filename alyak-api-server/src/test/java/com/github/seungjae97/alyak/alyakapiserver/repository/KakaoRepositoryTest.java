package com.github.seungjae97.alyak.alyakapiserver.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.auth.repository.KakaoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KakaoRepositoryTest {

    @Mock private RedisTemplate redisTemplate;
    @Mock private ValueOperations valueOperations;

    @InjectMocks private KakaoRepository kakaoRepository;

    @Test
    @DisplayName("액세스 토큰을 Redis에 저장한다")
    void saveAccessToken() {
        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        kakaoRepository.saveAccessToken("kakao:token:1", "access-token", 3600L);

        verify(valueOperations).set("kakao:token:1", "access-token", 3600L, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("Redis에서 액세스 토큰을 조회한다")
    void getAccessToken() {
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get("kakao:token:1")).willReturn("access-token");

        String token = kakaoRepository.getAccessToken("kakao:token:1");

        assertThat(token).isEqualTo("access-token");
        then(valueOperations).should().get("kakao:token:1");
    }
}
