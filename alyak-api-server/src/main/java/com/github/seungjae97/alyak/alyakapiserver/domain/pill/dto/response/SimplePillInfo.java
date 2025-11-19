package com.github.seungjae97.alyak.alyakapiserver.domain.pill.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SimplePillInfo {
    private String pillName;
    private Long pillId;
    private String manufacturer;
    private String ingredient;
}
