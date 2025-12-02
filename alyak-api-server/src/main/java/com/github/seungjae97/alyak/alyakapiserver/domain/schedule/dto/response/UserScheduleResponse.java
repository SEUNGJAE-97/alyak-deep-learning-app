package com.github.seungjae97.alyak.alyakapiserver.domain.schedule.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserScheduleResponse {
    private Long scheduleId;
    private String pillName;
    private String userMethod;
    private LocalDateTime scheduleTime;
    private LocalDateTime scheduleStartTime;
    private LocalDateTime scheduleEndTime;
    private Integer pillDosage;
}
