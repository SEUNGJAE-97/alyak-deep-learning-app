package com.github.seungjae97.alyak.alyakapiserver.domain.training.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.client.FastApiTrainingClient;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.client.dto.FastApiStartTrainingRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.client.dto.FastApiTrainingJobResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.dto.request.CreateTrainingJobRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.dto.response.TrainingJobResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.entity.TrainingJob;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.entity.TrainingJobStatus;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.repository.TrainingJobRepository;
import com.github.seungjae97.alyak.alyakapiserver.global.common.exception.BusinessError;
import com.github.seungjae97.alyak.alyakapiserver.global.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TrainingJobServiceImpl implements TrainingJobService {

    private final TrainingJobRepository trainingJobRepository;
    private final FastApiTrainingClient fastApiTrainingClient;
    private final ObjectMapper objectMapper;

    @Override
    public TrainingJobResponse createJob(CreateTrainingJobRequest request) {
        TrainingJob job = TrainingJob.builder()
                .status(TrainingJobStatus.PENDING)
                .datasetFilter(
                        request.getDatasetStatus() == null || request.getDatasetStatus().isBlank()
                                ? "TRAINING_SET"
                                : request.getDatasetStatus()
                )
                .paramsJson(toParamsJson(request))
                .progress(0)
                .message("Job created")
                .build();
        job = trainingJobRepository.save(job);

        try {
            FastApiTrainingJobResponse fastApiResponse = fastApiTrainingClient.startTraining(
                    FastApiStartTrainingRequest.builder()
                            .datasetStatus(job.getDatasetFilter())
                            .epochs(request.getEpochs())
                            .batchSize(request.getBatchSize())
                            .learningRate(request.getLearningRate())
                            .optimizer(request.getOptimizer())
                            .freezeLayers(request.getFreezeLayers())
                            .build()
            );

            if (fastApiResponse == null || fastApiResponse.getJobId() == null) {
                job.markFailed("FastAPI start response is empty");
            } else {
                job.markRunning(fastApiResponse.getJobId(), "Training started");
            }
        } catch (Exception e) {
            log.warn("Failed to start FastAPI training job. jobId={}", job.getId(), e);
            job.markFailed("FastAPI call failed: " + e.getMessage());
        }

        return TrainingJobResponse.from(trainingJobRepository.save(job));
    }

    @Override
    @Transactional(readOnly = true)
    public TrainingJobResponse getJob(Long id) {
        TrainingJob job = trainingJobRepository.findById(id)
                .orElseThrow(() -> new BusinessException(BusinessError.LABELING_ITEM_NOT_FOUND));
        return TrainingJobResponse.from(job);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TrainingJobResponse> getJobs(Pageable pageable) {
        return trainingJobRepository.findAll(pageable).map(TrainingJobResponse::from);
    }

    @Override
    public void syncRunningJobs() {
        List<TrainingJob> runningJobs = trainingJobRepository.findByStatus(TrainingJobStatus.RUNNING);
        for (TrainingJob job : runningJobs) {
            if (job.getExternalJobId() == null || job.getExternalJobId().isBlank()) {
                continue;
            }
            try {
                FastApiTrainingJobResponse status = fastApiTrainingClient.getJobStatus(job.getExternalJobId());
                if (status == null) {
                    continue;
                }
                job.syncStatus(
                        toInternalStatus(status.getStatus()),
                        status.getProgress(),
                        status.getMessage()
                );
                trainingJobRepository.save(job);
            } catch (Exception e) {
                log.warn("Training job sync failed. id={}, externalJobId={}", job.getId(), job.getExternalJobId(), e);
            }
        }
    }

    private TrainingJobStatus toInternalStatus(String fastApiStatus) {
        if (fastApiStatus == null || fastApiStatus.isBlank()) {
            return TrainingJobStatus.RUNNING;
        }
        try {
            return TrainingJobStatus.valueOf(fastApiStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            return TrainingJobStatus.RUNNING;
        }
    }

    private String toParamsJson(CreateTrainingJobRequest request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}
