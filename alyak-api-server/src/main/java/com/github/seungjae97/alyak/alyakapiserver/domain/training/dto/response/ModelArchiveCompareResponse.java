package com.github.seungjae97.alyak.alyakapiserver.domain.training.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class ModelArchiveCompareResponse {
    private String baseVersion;
    private String targetVersion;
    private List<MetricDelta> metrics;

    @Getter
    @Builder
    public static class MetricDelta {
        private String metric;
        private BigDecimal base;
        private BigDecimal target;
    }
}
