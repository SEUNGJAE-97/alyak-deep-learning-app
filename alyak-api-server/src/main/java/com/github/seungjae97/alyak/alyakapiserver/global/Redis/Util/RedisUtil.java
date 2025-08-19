package com.github.seungjae97.alyak.alyakapiserver.global.Redis.Util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisUtil {
    private final StringRedisTemplate template;

    public void setDataExpire(String key, String value, long seconds) {
        template.opsForValue().set(key, value, Duration.ofSeconds(seconds));
    }
    public String getData(String key) {
        return template.opsForValue().get(key);
    }
    public void deleteData(String key) {
        template.delete(key);
    }
    public boolean existData(String key) {
        return Boolean.TRUE.equals(template.hasKey(key));
    }
}
