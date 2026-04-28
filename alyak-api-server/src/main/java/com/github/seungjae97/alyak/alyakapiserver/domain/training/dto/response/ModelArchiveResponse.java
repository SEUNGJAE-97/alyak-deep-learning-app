package com.github.seungjae97.alyak.alyakapiserver.domain.training.dto.response;

import com.github.seungjae97.alyak.alyakapiserver.domain.training.entity.ModelArchive;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.entity.ModelArchiveStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class ModelArchiveResponse {
    private Long id;
    private String version;
    private LocalDateTime date;
    private ModelArchiveStatus status;
    private BigDecimal accuracy;
    private BigDecimal loss;
    private BigDecimal map;
    private String dataset;
    private Params params;

    @Getter
    @Builder
    public static class Params {
        private BigDecimal lr;
        private Integer batch;
        private Integer epochs;
        private String optimizer;
    }

    public static ModelArchiveResponse from(ModelArchive archive) {
        return ModelArchiveResponse.builder()
                .id(archive.getId())
                .version(archive.getVersion())
                .date(archive.getCreatedAt())
                .status(archive.getStatus())
                .accuracy(archive.getBestPrecision())
                .loss(archive.getBestLoss())
                .map(archive.getBestMap50())
                .dataset(archive.getDatasetName())
                .params(Params.builder()
                        .lr(archive.getLearningRate())
                        .batch(archive.getBatchSize())
                        .epochs(archive.getEpochs())
                        .optimizer(archive.getOptimizer())
                        .build())
                .build();
    }
}
