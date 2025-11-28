package com.github.seungjae97.alyak.alyakapiserver.domain.map.dto.response;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteResponse {
    private List<GeoPoint> path;

    @Data
    @AllArgsConstructor
    public static class GeoPoint {
        private double lat;
        private double lng;
    }
}
