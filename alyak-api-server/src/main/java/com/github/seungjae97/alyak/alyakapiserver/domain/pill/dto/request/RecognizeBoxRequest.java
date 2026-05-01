package com.github.seungjae97.alyak.alyakapiserver.domain.pill.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RecognizeBoxRequest {
    private Integer boxIndex;
    private BigDecimal xMin;
    private BigDecimal yMin;
    private BigDecimal xMax;
    private BigDecimal yMax;
}
