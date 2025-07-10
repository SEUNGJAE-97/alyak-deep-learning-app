package com.github.seungjae97.alyak.alyakapiserver.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class UserMedicationDto {
    private int id;
    private int userId;
    private int pillId;
    private int scheduleId;
    private int dosage;
    private Date startDate;
    private Date endDate;
}
