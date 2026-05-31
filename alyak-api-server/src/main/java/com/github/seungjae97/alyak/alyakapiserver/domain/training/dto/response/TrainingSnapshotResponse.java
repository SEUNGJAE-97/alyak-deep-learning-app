package com.github.seungjae97.alyak.alyakapiserver.domain.training.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.entity.TrainingSnapshot;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class TrainingSnapshotResponse {
    private String imagePath;
    private Integer boxIndex;

    @JsonProperty("xMin")
    private BigDecimal xMin;

    @JsonProperty("yMin")
    private BigDecimal yMin;

    @JsonProperty("xMax")
    private BigDecimal xMax;

    @JsonProperty("yMax")
    private BigDecimal yMax;

    public static TrainingSnapshotResponse from(TrainingSnapshot snapshot) {
        return TrainingSnapshotResponse.builder()
                .imagePath(snapshot.getImagePath())
                .boxIndex(snapshot.getBoxIndex())
                .xMin(snapshot.getXMin())
                .yMin(snapshot.getYMin())
                .xMax(snapshot.getXMax())
                .yMax(snapshot.getYMax())
                .build();
    }
}