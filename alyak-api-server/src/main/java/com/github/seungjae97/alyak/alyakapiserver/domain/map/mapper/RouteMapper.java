package com.github.seungjae97.alyak.alyakapiserver.domain.map.mapper;

import com.github.seungjae97.alyak.alyakapiserver.domain.map.dto.internal.RouteResult;
import com.github.seungjae97.alyak.alyakapiserver.domain.map.dto.response.RouteResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 내부 도메인 DTO를 클라이언트 응답 DTO로 변환하는 Mapper
 */
@Slf4j
@Component
public class RouteMapper {

    /**
     * RouteResult를 RouteResponse로 변환합니다.
     *
     * @param routeResult 내부 도메인 RouteResult
     * @return 클라이언트 응답 RouteResponse
     */
    public RouteResponse toRouteResponse(RouteResult routeResult) {
        if (routeResult == null) {
            return RouteResponse.builder()
                    .path(List.of())
                    .build();
        }

        // Location을 GeoPoint로 변환
        List<RouteResponse.GeoPoint> geoPoints = routeResult.getPath().stream()
                .map(location -> new RouteResponse.GeoPoint(location.getLat(), location.getLon()))
                .collect(Collectors.toList());

        return RouteResponse.builder()
                .path(geoPoints)
                .totalDistance(routeResult.getTotalDistance())
                .totalTime(routeResult.getTotalTime())
                .build();
    }
}

