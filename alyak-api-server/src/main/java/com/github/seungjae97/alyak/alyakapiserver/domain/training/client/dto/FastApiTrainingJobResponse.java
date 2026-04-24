package com.github.seungjae97.alyak.alyakapiserver.domain.training.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FastApiTrainingJobResponse {
    @JsonProperty("jobId")
    private String jobId;
    private String status;
    private Integer progress;
    private String message;
}
