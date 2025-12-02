package com.github.seungjae97.alyak.alyakapiserver.domain.pill.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PillDetailResponse {
    private Long pillId;
    private String pillName;
    private String pillImg;
    private String pillDescription;
    private String userMethod;
    private String pillEfficacy;
    private String pillWarn;
    private String pillCaution;
    private String pillInteractive;
    private String pillAdverseReaction;
    private String manufacturer;
}



