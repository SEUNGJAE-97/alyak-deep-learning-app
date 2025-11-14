package com.github.seungjae97.alyak.alyakapiserver.domain.pill.dto.response;

import lombok.Data;

@Data
public class SimplePillInfo {
    private String pillName;
    private String pillId;
    private String manufacturer;
    private String ingredient;
}
