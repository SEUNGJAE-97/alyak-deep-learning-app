package com.github.seungjae97.alyak.alyakapiserver.domain.map.dto.common;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 지리적 좌표를 나타내는 Value Object
 * 위도(latitude)와 경도(longitude)를 포함합니다.
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Location {
    private double lat;
    private double lon;
}

