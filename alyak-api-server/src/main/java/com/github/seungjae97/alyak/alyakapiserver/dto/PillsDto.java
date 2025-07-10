package com.github.seungjae97.alyak.alyakapiserver.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PillsDto {
    private int pillsId;
    private String identifier;
    private String formulation;
    private String size;
    private String productName;
    private String companyName;
    private int shapeId;
}
