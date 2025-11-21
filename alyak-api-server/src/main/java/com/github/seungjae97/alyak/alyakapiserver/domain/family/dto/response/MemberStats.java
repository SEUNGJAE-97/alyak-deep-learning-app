package com.github.seungjae97.alyak.alyakapiserver.domain.family.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberStats {
    /** 성공률 (0-100) */
    private Integer successRate;
    
    /** 완료 횟수 (TAKEN 상태) */
    private Integer completeCount;
    
    /** 놓친 횟수 (SKIPPED 상태) */
    private Integer missedCount;
    
    /** 지연 횟수 (TAKEN이지만 scheduleTime보다 늦게 복용) */
    private Integer delayedCount;
    
    /** 예정된 횟수 (SCHEDULED 상태) */
    private Integer scheduledCount;
}

