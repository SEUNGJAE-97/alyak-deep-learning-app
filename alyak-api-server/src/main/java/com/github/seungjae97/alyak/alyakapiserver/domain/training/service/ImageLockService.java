package com.github.seungjae97.alyak.alyakapiserver.domain.training.service;

public interface ImageLockService {
    void acquireLock(Long imageId, Long userId);
    void releaseLock(Long imageId, Long userId);
    void refreshLock(Long imageId, Long userId);
}
