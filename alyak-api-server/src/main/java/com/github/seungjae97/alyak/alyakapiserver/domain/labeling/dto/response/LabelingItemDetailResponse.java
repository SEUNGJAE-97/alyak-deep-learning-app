package com.github.seungjae97.alyak.alyakapiserver.domain.labeling.dto.response;

import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.entity.DataStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class LabelingItemDetailResponse {
    private Long id;
    private String imagePath;
    private DataStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Box> boxes;

    @Getter
    @Builder
    public static class Box {
        private Long id;
        private Integer boxIndex;
        private BigDecimal xMin;
        private BigDecimal yMin;
        private BigDecimal xMax;
        private BigDecimal yMax;
    }
}
