package com.github.seungjae97.alyak.alyakapiserver.domain.medication.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicationLogRequest {

    private String pillName;
    private Integer dosage;
    /** 원래 복용 예정 시각 */
    private LocalDateTime scheduledTime;
    /** 실제 복용 시각 (미복용이면 null) */
    private LocalDateTime takenTime;
}
