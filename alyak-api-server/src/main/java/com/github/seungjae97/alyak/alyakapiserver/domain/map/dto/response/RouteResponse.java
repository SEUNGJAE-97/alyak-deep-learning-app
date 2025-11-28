package com.github.seungjae97.alyak.alyakapiserver.domain.map.dto.response;

import lombok.*;

import java.util.List;

/**
 * 클라이언트에게 반환하는 경로 응답 DTO
 * 기존 API 호환성을 유지하면서 선택적으로 거리/시간 정보를 포함할 수 있습니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteResponse {
    /**
     * 경로 좌표 리스트
     */
    private List<GeoPoint> path;
    
    /**
     * 총 거리 (미터 단위, 선택적)
     */
    private Integer totalDistance;
    
    /**
     * 총 소요 시간 (초 단위, 선택적)
     */
    private Integer totalTime;

    @Data
    @AllArgsConstructor
    public static class GeoPoint {
        private double lat;
        private double lng;
    }
}
