package com.github.seungjae97.alyak.alyakapiserver.domain.training.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.entity.DataStatus;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.entity.PillImageData;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.repository.PillImageDataRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.client.FastApiTrainingClient;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.client.dto.FastApiStartTrainingRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.client.dto.FastApiTrainingJobResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.dto.request.CreateTrainingJobRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.dto.request.TrainingCompletionCallbackRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.dto.response.TrainingJobResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.dto.response.TrainingSnapshotResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.entity.ModelArchive;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.entity.TrainingJob;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.entity.TrainingJobStatus;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.entity.TrainingSnapshot;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.repository.ModelArchiveRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.repository.TrainingJobRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.repository.TrainingSnapshotRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.repository.TrainingSnapshotRepositoryImpl;
import com.github.seungjae97.alyak.alyakapiserver.global.common.exception.BusinessError;
import com.github.seungjae97.alyak.alyakapiserver.global.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TrainingJobServiceImpl implements TrainingJobService {

    private final TrainingJobRepository trainingJobRepository;
    private final ModelArchiveRepository modelArchiveRepository;
    private final FastApiTrainingClient fastApiTrainingClient;
    private final ObjectMapper objectMapper;
    private final PillImageDataRepository pillImageDataRepository;
    private final TrainingSnapshotRepository snapshotRepository;
    private final TrainingSnapshotRepositoryImpl snapshotRepositoryImpl;

    @Override
    public TrainingJobResponse createJob(CreateTrainingJobRequest request) {
        String jobUuid = UUID.randomUUID().toString();
        TrainingJob job = TrainingJob.builder()
                .status(TrainingJobStatus.PENDING)
                .externalJobId(jobUuid)
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
        String baseModelPath = resolveBaseModelPath(request.getBaseModelId());
        createSnapshot(job);

        try {
            FastApiTrainingJobResponse fastApiResponse = fastApiTrainingClient.startTraining(
                    FastApiStartTrainingRequest.builder()
                            .jobId(jobUuid)
                            .datasetStatus(job.getDatasetFilter())
                            .epochs(request.getEpochs())
                            .batchSize(request.getBatchSize())
                            .learningRate(request.getLearningRate())
                            .optimizer(request.getOptimizer())
                            .freezeLayers(request.getFreezeLayers())
                            .baseModelPath(baseModelPath)
                            .build()
            );

            if (fastApiResponse == null || fastApiResponse.getJobId() == null) {
                job.markFailed("FastAPI start response is empty");
            } else if (!jobUuid.equals(fastApiResponse.getJobId())) {
                job.markFailed("FastAPI returned mismatched jobId");
            } else {
                job.markStarted("Training started");
            }
        } catch (Exception e) {
            log.warn("Failed to start FastAPI training job. jobId={}", jobUuid, e);
            job.markFailed("FastAPI call failed: " + e.getMessage());
        }

        return TrainingJobResponse.from(trainingJobRepository.save(job));
    }

    private String resolveBaseModelPath(Long baseModelId) {
        if (baseModelId == null) {
            return null;
        }
        ModelArchive archive = modelArchiveRepository.findById(baseModelId)
                .orElseThrow(() -> new BusinessException(BusinessError.LABELING_ITEM_NOT_FOUND));
        return archive.getModelPath();
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
    public void completeByExternalJobId(String externalJobId, TrainingCompletionCallbackRequest request) {
        if (externalJobId == null || externalJobId.isBlank()) {
            return;
        }
        trainingJobRepository.findByExternalJobId(externalJobId).ifPresent(job -> {
            job.syncStatus(
                    toInternalStatus(request == null ? null : request.getStatus()),
                    request == null ? null : request.getProgress(),
                    request == null ? null : request.getMessage()
            );
            trainingJobRepository.save(job);
        });
    }

    @Transactional
    protected void createSnapshot(TrainingJob job) {
        // 1. Training_Set에 포함된 상태의 이미지들을 모두 가져온다.
        List<PillImageData> images = pillImageDataRepository.findAllByStatus(DataStatus.TRAINING_SET);
        // 2. 반복문을 돌면서 이미지-박스 조합을 스냅샷 row로 변환한다. (1 box = 1 row)
        List<TrainingSnapshot> snapshots = images.stream()
                .flatMap(image -> image.getBoxes().stream()
                        .map(box -> TrainingSnapshot.of(job, image, box)))
                .toList();

        snapshotRepositoryImpl.saveAll(snapshots);
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

    @Override
    @Transactional(readOnly = true)
    public Page<TrainingSnapshotResponse> getSnapshotByExternalJobId(String externalJobId, Pageable pageable) {
        TrainingJob job = trainingJobRepository.findByExternalJobId(externalJobId)
                .orElseThrow(() -> new BusinessException(BusinessError.LABELING_ITEM_NOT_FOUND));
        return snapshotRepository.findAllByTrainingJobId(job.getId(), pageable)
                .map(TrainingSnapshotResponse::from);
    }
}
