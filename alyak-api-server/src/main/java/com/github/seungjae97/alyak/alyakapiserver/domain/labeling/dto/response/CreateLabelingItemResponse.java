package com.github.seungjae97.alyak.alyakapiserver.domain.labeling.dto.response;

import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.entity.DataStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateLabelingItemResponse {
    private Long id;
    private DataStatus status;
    private Integer boxCount;
}
