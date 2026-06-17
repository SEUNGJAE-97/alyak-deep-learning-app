package com.github.seungjae97.alyak.alyakapiserver.domain.training.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TrainingLabelJsonPathResponse {
    private String jobId;
    private String labelJsonPath;
}
