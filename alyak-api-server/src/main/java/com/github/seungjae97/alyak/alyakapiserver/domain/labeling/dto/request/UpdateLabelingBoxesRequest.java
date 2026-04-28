package com.github.seungjae97.alyak.alyakapiserver.domain.labeling.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class UpdateLabelingBoxesRequest {
    private List<Box> boxes;

    @Getter
    @Setter
    public static class Box {
        private Integer boxIndex;
        @JsonProperty("xMin")
        private BigDecimal xMin;
        @JsonProperty("yMin")
        private BigDecimal yMin;
        @JsonProperty("xMax")
        private BigDecimal xMax;
        @JsonProperty("yMax")
        private BigDecimal yMax;
    }
}
