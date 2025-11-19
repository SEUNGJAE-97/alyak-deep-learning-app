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
    }

    public void setDataExpire(String key, String value) {
        template.opsForValue().set(key, value);
    }

    public String getData(String key) {
        String redisKey = PREFIX + key;
        return template.opsForValue().get(redisKey);
    }

    public void deleteData(String key) {
        String redisKey = PREFIX + key;
        template.delete(redisKey);
    }

    public boolean existData(String key) {
        String redisKey = PREFIX + key;
        return template.hasKey(redisKey);
    }
}
