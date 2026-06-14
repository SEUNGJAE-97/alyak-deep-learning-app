package com.github.seungjae97.alyak.alyakapiserver.domain.training.service;

import com.github.seungjae97.alyak.alyakapiserver.global.common.exception.BusinessError;
import com.github.seungjae97.alyak.alyakapiserver.global.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ImageLockServiceImpl implements ImageLockService {

    private final StringRedisTemplate redisTemplate;
    private static final long LOCK_TTL = 300L;
    private static final String LOCK_PREFIX = "lock:image:";

    /**
     * 라벨러가 이미지를 선택하는 순간 편집한다고 생각하고 lock을 걸도록 한다.
     *
     * @param imageId: 수정 하려는 이미지 아이디 값
     * @param userId   : 라벨러의 아이디 값
     */
    @Override
    public void acquireLock(Long imageId, Long userId) {
        String key = LOCK_PREFIX + imageId;

        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(key, String.valueOf(userId), LOCK_TTL, TimeUnit.SECONDS);

        if (!acquired) {
            String lockedBy = redisTemplate.opsForValue().get(key);
            throw new BusinessException(BusinessError.ALREADY_SOMEONE_EDIT);
        }

    }

    /**
     * 편집 완료 or 취소했을때
     *
     * @param imageId : 수정이 완료된 이미지 아이디 값
     * @param userId  : 라벨러의 아이디 값
     */
    @Override
    public void releaseLock(Long imageId, Long userId) {
        String key = LOCK_PREFIX + imageId;
        String owner = redisTemplate.opsForValue().get(key);

        if (String.valueOf(userId).equals(owner)) {
            redisTemplate.delete(key);
        }
    }

    @Override
    public void refreshLock(Long imageId, Long userId) {
        String key = LOCK_PREFIX + imageId;
        String owner = redisTemplate.opsForValue().get(key);

        if (String.valueOf(userId).equals(owner)) {
            redisTemplate.expire(key, LOCK_TTL, TimeUnit.SECONDS);
        }
    }
}
