package com.github.seungjae97.alyak.alyakapiserver.domain.pill.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RecognizeBoxRequest {
    @JsonProperty("boxIndex")
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
