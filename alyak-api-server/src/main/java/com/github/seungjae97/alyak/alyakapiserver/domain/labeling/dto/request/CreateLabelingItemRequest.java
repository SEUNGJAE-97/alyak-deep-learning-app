package com.github.seungjae97.alyak.alyakapiserver.domain.labeling.dto.request;

import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.entity.DataStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CreateLabelingItemRequest {
    private DataStatus status;
    private List<Box> boxes;

    @Getter
    @Setter
    public static class Box {
        private Integer boxIndex;
        private BigDecimal xMin;
        private BigDecimal yMin;
        private BigDecimal xMax;
        private BigDecimal yMax;
    }
}
