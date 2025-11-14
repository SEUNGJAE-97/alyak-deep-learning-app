package com.github.seungjae97.alyak.alyakapiserver.domain.pill.dto.request;

import lombok.Data;

@Data
public class PillSearchRequest {
    private String shape;
    private String color;
    private String score;
}
