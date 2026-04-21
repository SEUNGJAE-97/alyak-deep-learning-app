package com.github.seungjae97.alyak.alyakapiserver.domain.family.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class DailyMedicationStat {
    /** 날짜 */
    private LocalDate date;
    
    /** 성공 비율 (0.0-1.0) */
    private Float successRatio;
    
    /** 지연 비율 (0.0-1.0) */
    private Float delayedRatio;
    
    /** 놓친 비율 (0.0-1.0) */
    private Float missedRatio;
}

