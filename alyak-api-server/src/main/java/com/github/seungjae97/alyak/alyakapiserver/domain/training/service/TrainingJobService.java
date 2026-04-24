package com.github.seungjae97.alyak.alyakapiserver.domain.training.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.training.dto.request.CreateTrainingJobRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.dto.response.TrainingJobResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TrainingJobService {
    TrainingJobResponse createJob(CreateTrainingJobRequest request);

    TrainingJobResponse getJob(Long id);

    Page<TrainingJobResponse> getJobs(Pageable pageable);

    void syncRunningJobs();
}
