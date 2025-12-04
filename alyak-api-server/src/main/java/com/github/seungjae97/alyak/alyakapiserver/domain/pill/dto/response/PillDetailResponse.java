package com.github.seungjae97.alyak.alyakapiserver.domain.pill.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    // 누락된 속성값
    private String pillClassification;
    private String pillType;
    private List<String> efficacyTags;
    private List<String> specialCautionTags;
    private List<String> alertItems;
}



