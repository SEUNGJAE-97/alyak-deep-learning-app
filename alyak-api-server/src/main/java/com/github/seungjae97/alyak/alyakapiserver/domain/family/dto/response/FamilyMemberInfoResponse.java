package com.github.seungjae97.alyak.alyakapiserver.domain.family.dto.response;

import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FamilyMemberInfoResponse {
    private Long memberId;
    private String memberName;

    public static FamilyMemberInfoResponse from(User user) {
        return FamilyMemberInfoResponse.builder()
                .memberId(user.getId())
                .memberName(user.getName())
                .build();
    }
}
