package com.github.seungjae97.alyak.alyakapiserver.domain.pill.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimplePillInfo {
    private String pillName;
    private Long pillId;
    private String manufacturer;
    private String classification;
    private String pillType;
    private String pillImg;
}
