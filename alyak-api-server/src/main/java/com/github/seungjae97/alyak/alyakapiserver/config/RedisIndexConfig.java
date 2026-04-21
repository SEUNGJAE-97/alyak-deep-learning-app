package com.github.seungjae97.alyak.alyakapiserver.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.search.FTCreateParams;
import redis.clients.jedis.search.IndexDataType;
import redis.clients.jedis.search.schemafields.TagField;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisIndexConfig implements ApplicationRunner {

    private final JedisPooled jedis;

    @Override
    public void run(ApplicationArguments args) {
        try {
            jedis.ftDropIndex("pill_idx");
        } catch (Exception ignored) {
        }

        try {
            jedis.ftCreate(
                    "pill_idx",
                    FTCreateParams.createParams()
                            .on(IndexDataType.HASH)
                            .prefix("pill:"),
                    TagField.of("name").withSuffixTrie(),
                    TagField.of("name_cho").withSuffixTrie(),
                    TagField.of("name_en").withSuffixTrie(),
                    TagField.of("ingredient_cho").withSuffixTrie()
            );
            log.info("인덱스 생성 완료");
        } catch (Exception e) {
            log.error("인덱스 생성 실패: {}", e.getMessage());
        }
    }
}
