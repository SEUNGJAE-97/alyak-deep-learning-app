package com.github.seungjae97.alyak.alyakapiserver.domain.map.dto.request;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RouteRequest {
    private double startLat;
    private double startLng;
    private double endLat;
    private double endLng;
}
