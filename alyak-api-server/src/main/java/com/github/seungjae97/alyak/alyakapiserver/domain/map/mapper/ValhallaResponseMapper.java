package com.github.seungjae97.alyak.alyakapiserver.domain.map.mapper;

import com.github.seungjae97.alyak.alyakapiserver.domain.map.dto.common.Location;
import com.github.seungjae97.alyak.alyakapiserver.domain.map.dto.external.valhalla.ValhallaRouteResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.map.dto.internal.RouteResult;
import com.github.seungjae97.alyak.alyakapiserver.domain.map.infrastructure.util.PolylineDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Valhalla API 응답을 내부 도메인 DTO로 변환하는 Mapper
 */
@Slf4j
@Component
public class ValhallaResponseMapper {

    /**
     * Valhalla 응답을 RouteResult로 변환합니다.
     *
     * @param valhallaResponse Valhalla API 응답
     * @return 내부 도메인 RouteResult
     * @throws IllegalArgumentException 응답이 유효하지 않은 경우
     */
    public RouteResult toRouteResult(ValhallaRouteResponse valhallaResponse) {
        if (valhallaResponse == null || valhallaResponse.getTrip() == null) {
            throw new IllegalArgumentException("Valhalla 응답이 null이거나 trip 정보가 없습니다.");
        }

        ValhallaRouteResponse.Trip trip = valhallaResponse.getTrip();
        if (trip.getLegs() == null || trip.getLegs().isEmpty()) {
            throw new IllegalArgumentException("Valhalla 응답에 leg 정보가 없습니다.");
        }

        // 첫 번째 leg 사용 (일반적으로 하나의 leg만 존재)
        ValhallaRouteResponse.Leg leg = trip.getLegs().get(0);

        // 폴리라인 디코딩
        String encodedShape = leg.getShape();
        if (encodedShape == null || encodedShape.isEmpty()) {
            log.warn("Valhalla 응답에 shape 정보가 없습니다.");
            throw new IllegalArgumentException("Valhalla 응답에 shape 정보가 없습니다.");
        }

        List<Location> path = PolylineDecoder.decode(encodedShape);

        // Summary에서 거리와 시간 추출
        ValhallaRouteResponse.Summary summary = leg.getSummary();
        if (summary == null) {
            throw new IllegalArgumentException("Valhalla 응답에 summary 정보가 없습니다.");
        }

        // Valhalla는 length를 킬로미터 단위로 제공하므로 미터로 변환
        int totalDistance = (int) Math.round(summary.getLength() * 1000);

        // Valhalla는 time을 초 단위로 제공
        int totalTime = (int) Math.round(summary.getTime());

        return RouteResult.builder()
                .path(path)
                .totalDistance(totalDistance)
                .totalTime(totalTime)
                .build();
    }
}

