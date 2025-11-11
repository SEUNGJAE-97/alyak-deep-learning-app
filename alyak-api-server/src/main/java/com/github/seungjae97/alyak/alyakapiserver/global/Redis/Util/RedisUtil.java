package com.github.seungjae97.alyak.alyakapiserver.global.Redis.Util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisUtil {
    private final StringRedisTemplate template;
    private static final String PREFIX = "email:";

    public void setDataExpire(String key, String value, long seconds) {
        template.opsForValue().set(PREFIX + key, value, Duration.ofSeconds(seconds));
    }

    public String getData(String key) {
        return template.opsForValue().get(PREFIX+key);
    }

    public void deleteData(String key) {
        template.delete(PREFIX+key);
    }

    public boolean existData(String key) {
        return Boolean.TRUE.equals(template.hasKey(PREFIX+key));
    }
}
