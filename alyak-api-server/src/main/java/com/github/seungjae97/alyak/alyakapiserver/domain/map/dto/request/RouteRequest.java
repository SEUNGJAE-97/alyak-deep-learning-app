package com.github.seungjae97.alyak.alyakapiserver.domain.map.dto.request;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * 경로 조회 요청 DTO
 * 출발지 좌표와 목적지 정보를 포함합니다.
 */
@Data
@RequiredArgsConstructor
public class RouteRequest {
    private double startLat;
    private double startLng;
    private double endLat;
    private double endLng;
    
    /**
     * 목적지 ID (병원, 시설 등)
     * Spatial Caching을 위한 키로 사용됩니다.
     */
    private Long destinationId;
}
