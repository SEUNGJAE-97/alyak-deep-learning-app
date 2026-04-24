package com.github.seungjae97.alyak.alyakapiserver.domain.training.dto.response;

import com.github.seungjae97.alyak.alyakapiserver.domain.training.entity.TrainingJob;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.entity.TrainingJobStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TrainingJobResponse {
    private Long id;
    private TrainingJobStatus status;
    private String datasetFilter;
    private String paramsJson;
    private String externalJobId;
    private Integer progress;
    private String message;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TrainingJobResponse from(TrainingJob job) {
        return TrainingJobResponse.builder()
                .id(job.getId())
                .status(job.getStatus())
                .datasetFilter(job.getDatasetFilter())
                .paramsJson(job.getParamsJson())
                .externalJobId(job.getExternalJobId())
                .progress(job.getProgress())
                .message(job.getMessage())
                .startedAt(job.getStartedAt())
                .finishedAt(job.getFinishedAt())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }
}
