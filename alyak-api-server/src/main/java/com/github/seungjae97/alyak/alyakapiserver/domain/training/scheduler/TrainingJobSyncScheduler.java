package com.github.seungjae97.alyak.alyakapiserver.domain.training.scheduler;

import com.github.seungjae97.alyak.alyakapiserver.domain.training.service.TrainingJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TrainingJobSyncScheduler {

    private final TrainingJobService trainingJobService;

    @Scheduled(fixedDelayString = "${training.sync.fixed-delay-ms:5000}")
    public void syncRunningJobs() {
        try {
            trainingJobService.syncRunningJobs();
        } catch (Exception e) {
            log.warn("Training job sync scheduler failed", e);
        }
    }
}
