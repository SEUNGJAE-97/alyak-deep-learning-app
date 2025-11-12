package com.github.seungjae97.alyak.alyakapiserver.global.Redis.Util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisUtil {
    private final StringRedisTemplate template;
    private static final String PREFIX = "email:";

    public void setDataExpire(String key, String value, long seconds) {
        String redisKey = PREFIX + key;
        template.opsForValue().set(redisKey, value, Duration.ofSeconds(seconds));
        log.info("[Redis 저장] 키: {}, 값: {}, 만료시간: {}초", redisKey, value, seconds);
    }

    public String getData(String key) {
        String redisKey = PREFIX + key;
        String value = template.opsForValue().get(redisKey);
        log.info("[Redis 조회] 키: {}, 값: {}", redisKey, value != null ? value : "null");
        return value;
    }

    public void deleteData(String key) {
        String redisKey = PREFIX + key;
        template.delete(redisKey);
        log.info("[Redis 삭제] 키: {}", redisKey);
    }

    public boolean existData(String key) {
        String redisKey = PREFIX + key;
        boolean exists = template.hasKey(redisKey);
        log.info("[Redis 존재 확인] 키: {}, 존재: {}", redisKey, exists);
        return exists;
    }
}
