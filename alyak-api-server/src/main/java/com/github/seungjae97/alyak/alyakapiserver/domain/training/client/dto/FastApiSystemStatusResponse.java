package com.github.seungjae97.alyak.alyakapiserver.domain.training.client.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FastApiSystemStatusResponse {
    private String status;
    private Boolean connected;
    private String message;
    private String device;
    private String cpuName;
    private Integer cpuCores;
    private Double cpuLoadPercent;
    private Boolean gpuAvailable;
    private String gpuName;
    private Integer gpuMemoryTotalMb;
    private Integer gpuMemoryFreeMb;
    private Integer gpuMemoryUsedMb;
    private Integer runningJobs;
    private Integer pendingJobs;
}
