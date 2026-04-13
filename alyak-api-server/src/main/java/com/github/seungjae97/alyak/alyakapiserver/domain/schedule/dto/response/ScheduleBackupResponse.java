package com.github.seungjae97.alyak.alyakapiserver.domain.schedule.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleBackupResponse {

    private Long scheduleId;
    private Long pillId;
    private String pillName;
    private Integer dosage;
    private LocalTime scheduledTime;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
}
