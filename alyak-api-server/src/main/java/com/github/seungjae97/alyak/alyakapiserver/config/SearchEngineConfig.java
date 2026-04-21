package com.github.seungjae97.alyak.alyakapiserver.config;

import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.Pill;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.repository.PillRepository;
import com.github.seungjae97.alyak.alyakapiserver.global.util.HangulUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchEngineConfig implements ApplicationRunner {
    private final PillRepository pillRepository;
    private final StringRedisTemplate redisTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Redis Hash 적재 시작");
        Set<String> keys = redisTemplate.keys("pill:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
        List<Pill> pillList = pillRepository.findAll();
        log.info("알약 수: {}", pillList.size());
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();

        for (Pill pill : pillList) {
            String key = "pill:" + pill.getId();
            Map<String, String> fields = new HashMap<>();
            String pillName = pill.getPillName() == null ? "" : pill.getPillName();
            String ingredient = pill.getPillIngredient() == null ? "" : pill.getPillIngredient();

            fields.put("name", pillName);
            fields.put("name_cho", HangulUtils.decompose(pillName));
            fields.put("name_en", "");
            fields.put("ingredient", ingredient.toLowerCase());
            fields.put("ingredient_cho", HangulUtils.decompose(ingredient));

            hashOps.putAll(key, fields);
        }
        log.info("Hash 적재 완료");
    }
}
