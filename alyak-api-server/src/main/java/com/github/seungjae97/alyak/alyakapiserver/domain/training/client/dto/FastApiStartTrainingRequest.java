package com.github.seungjae97.alyak.alyakapiserver.domain.training.client.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FastApiStartTrainingRequest {
    private String datasetStatus;
    private Integer epochs;
    private Integer batchSize;
    private Double learningRate;
    private String optimizer;
    private String freezeLayers;
}
