package com.github.seungjae97.alyak.alyakapiserver.domain.family.dto.response;

import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FamilyMemberInfoResponse {
    /** 가족 내 역할 (예: "본인", "아버지", "아들" 등) */
    private String role;
    
    /** 구성원 이름 */
    private String name;
    
    /** 통계 정보 */
    private MemberStats stats;
    
    /** 주간 일별 복약 통계 (최근 7일) */
    private List<DailyMedicationStat> weeklyMedicationStats;

    public static FamilyMemberInfoResponse from(User user) {
        return FamilyMemberInfoResponse.builder()
                .name(user.getName())
                .role("가족 구성원")
                .build();
    }
}
