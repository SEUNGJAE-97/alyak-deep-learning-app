package com.github.seungjae97.alyak.alyakapiserver.domain.pill.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PillDto {
    private String name;
    private String ingredient;
    private String nameEn;
}
