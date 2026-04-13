package com.github.seungjae97.alyak.alyakapiserver.config;

import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.Pill;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.repository.PillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import java.util.List;

import static com.github.seungjae97.alyak.alyakapiserver.global.util.HangulUtils.decompose;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchEngineConfig implements ApplicationRunner {
    private final PillRepository pillRepository;
    private final StringRedisTemplate redisTemplate;
    private static final String AUTOCOMPLETE_KEY = "autocomplete";

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("SearchEngineService init");
        redisTemplate.delete(AUTOCOMPLETE_KEY);

        // 1. 주입 알약 이름 목록 모두 가져오기
        List<Pill> pillList = pillRepository.findAll();
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();

        // 2. 자소 분리 후 Redis에 저장
        for(Pill pill : pillList) {
            String originalName = pill.getPillName();
            String decomposed1 = decompose(originalName);
            zSetOps.add(AUTOCOMPLETE_KEY, decomposed1 + ":" + originalName, 0.0);

            String stripedName = originalName.replaceAll("정(?=\\d)|캡슐|시럽", "");

            if (!stripedName.equals(originalName)) {
                String decomposed2 = decompose(stripedName);
                zSetOps.add(AUTOCOMPLETE_KEY, decomposed2 + ":" + originalName, 0.0);
            }
        }

        log.info("SearchEngineService init End");
    }
}
