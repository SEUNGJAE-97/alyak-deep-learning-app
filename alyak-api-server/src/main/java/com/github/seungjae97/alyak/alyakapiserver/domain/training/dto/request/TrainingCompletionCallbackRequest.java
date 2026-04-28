package com.github.seungjae97.alyak.alyakapiserver.domain.training.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainingCompletionCallbackRequest {
    private String status;
    private Integer progress;
    private String message;
}
