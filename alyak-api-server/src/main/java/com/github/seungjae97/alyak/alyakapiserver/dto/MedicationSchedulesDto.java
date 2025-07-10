package com.github.seungjae97.alyak.alyakapiserver.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
public class MedicationSchedulesDto {
    private int idx;
    private int userId;
    private Date startDate;
    private Date endDate;
    private int timesPerDay;
    private String memo;
    private Timestamp createdAt;
}
