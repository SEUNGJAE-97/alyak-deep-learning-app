package com.github.seungjae97.alyak.alyakapiserver.domain.training.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.training.dto.request.CreateTrainingJobRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.dto.request.TrainingCompletionCallbackRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.dto.response.TrainingJobResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.dto.response.TrainingSnapshotResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.entity.TrainingJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TrainingJobService {
    TrainingJobResponse createJob(CreateTrainingJobRequest request);

    TrainingJobResponse getJob(Long id);

    Page<TrainingJobResponse> getJobs(Pageable pageable);

    void completeByExternalJobId(String externalJobId, TrainingCompletionCallbackRequest request);

    Page<TrainingSnapshotResponse> getSnapshotByExternalJobId(String externalJobId, Pageable pageable);
}
