package com.github.seungjae97.alyak.alyakapiserver.domain.training.dto.response;

import com.github.seungjae97.alyak.alyakapiserver.domain.training.entity.ModelArchive;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class ModelArchiveDetailResponse {
    private ModelArchiveResponse summary;
    private String runDir;
    private String modelPath;
    private String argsPath;
    private String resultsPath;
    private Integer imageCount;
    private String augmentationSummary;
    private BigDecimal map50_95;
    private BigDecimal precision;
    private BigDecimal recall;
    private BigDecimal fitness;
    private String freezeLayers;
    private LocalDateTime updatedAt;

    public static ModelArchiveDetailResponse from(ModelArchive archive) {
        return ModelArchiveDetailResponse.builder()
                .summary(ModelArchiveResponse.from(archive))
                .runDir(archive.getRunDir())
                .modelPath(archive.getModelPath())
                .argsPath(archive.getArgsPath())
                .resultsPath(archive.getResultsPath())
                .imageCount(archive.getImageCount())
                .augmentationSummary(archive.getAugmentationSummary())
                .map50_95(archive.getBestMap50_95())
                .precision(archive.getBestPrecision())
                .recall(archive.getBestRecall())
                .fitness(archive.getBestFitness())
                .freezeLayers(archive.getFreezeLayers())
                .updatedAt(archive.getUpdatedAt())
                .build();
    }
}
