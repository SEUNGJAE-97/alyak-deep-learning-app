package com.github.seungjae97.alyak.alyakapiserver.domain.family.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AcceptFamilyInviteRequest {

    /** 초대한 사용자 ID (FCM data.inviterUserId) */
    private Long inviterUserId;
}
