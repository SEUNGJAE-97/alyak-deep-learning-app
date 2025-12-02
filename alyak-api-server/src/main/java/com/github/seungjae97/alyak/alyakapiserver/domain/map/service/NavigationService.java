package com.github.seungjae97.alyak.alyakapiserver.domain.map.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.seungjae97.alyak.alyakapiserver.domain.map.cache.RedisRouteService;
import com.github.seungjae97.alyak.alyakapiserver.domain.map.dto.common.Location;
import com.github.seungjae97.alyak.alyakapiserver.domain.map.dto.external.valhalla.ValhallaRouteResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.map.dto.internal.RouteResult;
import com.github.seungjae97.alyak.alyakapiserver.domain.map.dto.request.RouteRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.map.infrastructure.client.ValhallaClient;
import com.github.seungjae97.alyak.alyakapiserver.domain.map.mapper.RouteMapper;
import com.github.seungjae97.alyak.alyakapiserver.domain.map.mapper.ValhallaResponseMapper;
import com.github.seungjae97.alyak.alyakapiserver.domain.map.dto.response.RouteResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 경로 탐색 메인 서비스
 * Spatial Caching 전략을 구현하여 캐시 히트 시 Valhalla를 사용한 First Mile 계산과 경로 병합을 수행합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NavigationService {

    private final RedisRouteService redisRouteService;
    private final ValhallaClient valhallaClient;
    private final ExternalMapApi externalMapApi;
    private final ValhallaResponseMapper valhallaResponseMapper;
    private final RouteMapper routeMapper;
    private final ObjectMapper objectMapper;

    /**
     * 경로를 조회합니다.
     * 1. Redis 캐시 확인 (500m 반경)
     * 2. Cache Hit: Valhalla First Mile + 캐시 경로 병합
     * 3. Cache Miss: 외부 API 호출 및 캐싱
     *
     * @param request 경로 조회 요청
     * @return 경로 응답
     */
    public RouteResponse findRoute(RouteRequest request) {
        // destinationId가 없으면 기존 방식으로 처리 (캐싱 없음)
        if (request.getDestinationId() == null) {
            log.info("destinationId가 없어 외부 API를 직접 호출합니다.");
            RouteResult routeResult = externalMapApi.getRoute(request);
            return routeMapper.toRouteResponse(routeResult);
        }

        // 1. Redis 캐시 확인 (500m 이내 출발점 찾기)
        RedisRouteService.CachedRouteInfo cachedInfo = redisRouteService.findNearestRoute(
                request.getDestinationId(),
                request.getStartLat(),
                request.getStartLng()
        );

        if (cachedInfo != null) {
            log.info("🎯 Cache HIT! (Distance: {}m)", cachedInfo.getDistanceToUser());
            RouteResult hybridRoute = getHybridRoute(request, cachedInfo);
            return routeMapper.toRouteResponse(hybridRoute);
        } else {
            log.info("📡 Cache MISS. Calling External API...");
            RouteResult newRoute = getNewRouteAndCache(request);
            return routeMapper.toRouteResponse(newRoute);
        }
    }

    /**
     * [Cache Hit] 하이브리드 경로 생성
     * Valhalla로 First Mile 계산 후 캐시된 경로와 병합합니다.
     *
     * @param request 사용자 요청
     * @param cachedInfo 캐시된 경로 정보
     * @return 병합된 경로 결과
     */
    private RouteResult getHybridRoute(RouteRequest request, RedisRouteService.CachedRouteInfo cachedInfo) {
        // Step A: Valhalla로 First Mile 계산 (내 위치 -> 캐시 시작점)
        RouteResult firstMilePath = callValhalla(
                request.getStartLat(), request.getStartLng(),
                cachedInfo.getStartLat(), cachedInfo.getStartLon()
        );

        // Step B: Redis에서 Main 경로 가져오기
        RouteResult mainPath = redisRouteService.getRouteData(cachedInfo.getRouteId());
        if (mainPath == null) {
            log.warn("캐시된 경로 데이터를 찾을 수 없어 외부 API를 호출합니다.");
            return getNewRouteAndCache(request);
        }

        // Step C: 두 경로 병합 (Stitching)
        return mergePaths(firstMilePath, mainPath);
    }

    /**
     * [Cache Miss] 외부 API 호출 및 저장
     *
     * @param request 사용자 요청
     * @return 경로 결과
     */
    private RouteResult getNewRouteAndCache(RouteRequest request) {
        // 외부 API 호출 (Tmap 등)
        RouteResult fullPath = externalMapApi.getRoute(request);

        // Redis에 저장 (다음 사용자를 위해)
        redisRouteService.saveRoute(
                request.getDestinationId(),
                request.getStartLat(),
                request.getStartLng(),
                fullPath
        );

        return fullPath;
    }

    /**
     * Valhalla API를 호출하여 경로를 계산합니다.
     *
     * @param startLat 출발지 위도
     * @param startLon 출발지 경도
     * @param endLat 목적지 위도
     * @param endLon 목적지 경도
     * @return 경로 결과
     */
    private RouteResult callValhalla(double startLat, double startLon, double endLat, double endLon) {
        try {
            // Valhalla JSON 요청 생성
            String jsonRequest = String.format(
                    "{\"locations\":[{\"lat\":%f,\"lon\":%f},{\"lat\":%f,\"lon\":%f}],\"costing\":\"pedestrian\",\"directions_options\":{\"units\":\"km\"}}",
                    startLat, startLon, endLat, endLon
            );

            // Valhalla API 호출
            String responseJson = valhallaClient.getRoute(jsonRequest);

            // JSON 파싱
            ValhallaRouteResponse valhallaResponse = objectMapper.readValue(
                    responseJson,
                    ValhallaRouteResponse.class
            );

            // RouteResult로 변환
            return valhallaResponseMapper.toRouteResult(valhallaResponse);
        } catch (Exception e) {
            log.error("Valhalla API 호출 실패", e);
            throw new RuntimeException("Valhalla 경로 계산 실패", e);
        }
    }

    /**
     * 두 경로를 병합합니다.
     * First Mile 경로와 캐시된 Main 경로를 연결합니다.
     *
     * @param firstMile First Mile 경로 (사용자 위치 -> 캐시 시작점)
     * @param mainPath Main 경로 (캐시 시작점 -> 목적지)
     * @return 병합된 경로
     */
    private RouteResult mergePaths(RouteResult firstMile, RouteResult mainPath) {
        List<Location> mergedPath = new ArrayList<>();

        // First Mile 경로 추가
        if (firstMile.getPath() != null) {
            mergedPath.addAll(firstMile.getPath());
        }

        // Main 경로 추가 (첫 번째 포인트는 중복될 수 있으므로 제외할 수도 있음)
        if (mainPath.getPath() != null && !mainPath.getPath().isEmpty()) {
            // 첫 번째 포인트가 이전 경로의 마지막 포인트와 매우 가까우면 제외
            if (!mergedPath.isEmpty()) {
                Location lastPoint = mergedPath.get(mergedPath.size() - 1);
                Location firstMainPoint = mainPath.getPath().get(0);

                // 거리 계산 (간단한 유클리드 거리)
                double distance = calculateDistance(lastPoint, firstMainPoint);
                if (distance > 10.0) { // 10m 이상 떨어져 있으면 추가
                    mergedPath.addAll(mainPath.getPath());
                } else {
                    // 첫 번째 포인트 제외하고 나머지 추가
                    mergedPath.addAll(mainPath.getPath().subList(1, mainPath.getPath().size()));
                }
            } else {
                mergedPath.addAll(mainPath.getPath());
            }
        }

        // 거리와 시간 합산
        int totalDistance = firstMile.getTotalDistance() + mainPath.getTotalDistance();
        int totalTime = firstMile.getTotalTime() + mainPath.getTotalTime();

        return RouteResult.builder()
                .path(mergedPath)
                .totalDistance(totalDistance)
                .totalTime(totalTime)
                .build();
    }

    /**
     * 두 좌표 간의 거리를 계산합니다 (미터 단위, 간단한 유클리드 거리).
     *
     * @param point1 첫 번째 좌표
     * @param point2 두 번째 좌표
     * @return 거리 (미터)
     */
    private double calculateDistance(Location point1, Location point2) {
        // 간단한 하버사인 공식 (정확도는 낮지만 빠름)
        final int EARTH_RADIUS_M = 6371000; // 지구 반지름 (미터)

        double lat1Rad = Math.toRadians(point1.getLat());
        double lat2Rad = Math.toRadians(point2.getLat());
        double deltaLat = Math.toRadians(point2.getLat() - point1.getLat());
        double deltaLon = Math.toRadians(point2.getLon() - point1.getLon());

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_M * c;
    }
}

