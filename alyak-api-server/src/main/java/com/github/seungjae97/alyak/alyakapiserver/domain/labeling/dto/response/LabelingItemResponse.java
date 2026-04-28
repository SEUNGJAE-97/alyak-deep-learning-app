package com.github.seungjae97.alyak.alyakapiserver.domain.labeling.dto.response;

import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.entity.DataStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LabelingItemResponse {
    private Long id;
    private String imagePath;
    private DataStatus status;
    private Integer boxCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
