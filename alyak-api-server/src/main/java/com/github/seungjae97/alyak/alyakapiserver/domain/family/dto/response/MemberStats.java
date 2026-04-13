package com.github.seungjae97.alyak.alyakapiserver.domain.family.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberStats {
    /** 성공률 (0-100) */
    private Integer successRate;
    
    /** 복용 완료 횟수 (TAKEN + 지연 복용 DELAYED 포함, 성공률 분모에 사용) */
    private Integer completeCount;
    
    /** 놓친 횟수 (SKIPPED 상태) */
    private Integer missedCount;
    
    /** 지연 횟수 (DELAYED 상태) */
    private Integer delayedCount;
}

