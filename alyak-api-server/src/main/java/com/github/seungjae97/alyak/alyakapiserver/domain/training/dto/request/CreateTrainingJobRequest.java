package com.github.seungjae97.alyak.alyakapiserver.domain.training.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTrainingJobRequest {
    private String datasetStatus;
    private Integer epochs;
    private Integer batchSize;
    private Double learningRate;
    private String optimizer;
    private String freezeLayers;
    private Long baseModelId;
}
