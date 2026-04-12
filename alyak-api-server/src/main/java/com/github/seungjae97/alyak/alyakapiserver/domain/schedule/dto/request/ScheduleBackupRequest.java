package com.github.seungjae97.alyak.alyakapiserver.domain.schedule.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleBackupRequest {

    private Long pillId;
    private String pillName;
    private Integer dosage;
    private LocalTime scheduledTime;
    private LocalDate startDate;
    private LocalDate endDate;
}
