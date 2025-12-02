package com.github.seungjae97.alyak.alyakapiserver.domain.map.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.seungjae97.alyak.alyakapiserver.domain.map.dto.internal.RouteResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Redis를 사용한 공간 캐싱 서비스
 * GEO 인덱스를 활용하여 목적지 주변의 캐싱된 경로를 검색합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisRouteService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String GEO_KEY_PREFIX = "routes:geo:";
    private static final String DATA_KEY_PREFIX = "routes:data:";
    private static final int CACHE_EXPIRY_HOURS = 24;
    private static final double SEARCH_RADIUS_KM = 0.5; // 500m 반경

    /**
     * 경로를 Redis에 저장합니다.
     * GEO 인덱스와 실제 데이터를 분리하여 저장합니다.
     *
     * @param destinationId 목적지 ID (병원, 시설 등)
     * @param startLat 출발지 위도
     * @param startLon 출발지 경도
     * @param routeData 저장할 경로 데이터
     */
    public void saveRoute(Long destinationId, double startLat, double startLon, RouteResult routeData) {
        String routeId = UUID.randomUUID().toString();

        try {
            // A. 공간 인덱스 저장 (Key: routes:geo:{destinationId}, Member: routeId)
            String geoKey = GEO_KEY_PREFIX + destinationId;
            Point startPoint = new Point(startLon, startLat); // Redis GEO는 (경도, 위도) 순서
            redisTemplate.opsForGeo().add(geoKey, startPoint, routeId);

            // B. 실제 경로 데이터 저장 (Key: routes:data:{routeId})
            String dataKey = DATA_KEY_PREFIX + routeId;
            String json = objectMapper.writeValueAsString(routeData);
            redisTemplate.opsForValue().set(dataKey, json, CACHE_EXPIRY_HOURS, TimeUnit.HOURS);

            log.debug("경로 캐시 저장 완료: destinationId={}, routeId={}", destinationId, routeId);
        } catch (JsonProcessingException e) {
            log.error("경로 데이터 직렬화 실패", e);
            throw new RuntimeException("경로 캐시 저장 실패", e);
        }
    }

    /**
     * 현재 위치 기준 500m 반경 내의 가장 가까운 캐싱된 경로를 찾습니다.
     *
     * @param destinationId 목적지 ID
     * @param currentLat 현재 위치 위도
     * @param currentLon 현재 위치 경도
     * @return 캐싱된 경로 정보 (없으면 null)
     */
    public CachedRouteInfo findNearestRoute(Long destinationId, double currentLat, double currentLon) {
        String geoKey = GEO_KEY_PREFIX + destinationId;

        // Redis GEO 반경 검색 (500m, 가장 가까운 1개)
        Circle searchCircle = new Circle(
                new Point(currentLon, currentLat),
                new Distance(SEARCH_RADIUS_KM, Metrics.KILOMETERS)
        );

        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate.opsForGeo().radius(
                geoKey,
                searchCircle,
                RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                        .includeDistance()
                        .sortAscending()
                        .limit(1)
        );

        if (results == null || results.getContent().isEmpty()) {
            log.debug("캐시 미스: destinationId={}, 위치=({}, {})", destinationId, currentLat, currentLon);
            return null;
        }

        // 가장 가까운 결과 추출
        GeoResult<RedisGeoCommands.GeoLocation<String>> result = results.getContent().get(0);
        String routeId = result.getContent().getName();
        Distance distance = result.getDistance();

        // 캐시된 경로의 실제 시작점 좌표 조회
        List<Point> positions = redisTemplate.opsForGeo().position(geoKey, routeId);
        if (positions == null || positions.isEmpty()) {
            log.warn("캐시된 경로의 좌표를 찾을 수 없음: routeId={}", routeId);
            return null;
        }

        Point startPoint = positions.get(0);
        double distanceToUser = distance.getValue(); // 미터 단위

        log.info("캐시 히트: destinationId={}, routeId={}, 거리={}m", destinationId, routeId, distanceToUser);

        return new CachedRouteInfo(routeId, startPoint.getY(), startPoint.getX(), distanceToUser);
    }

    /**
     * 저장된 경로 데이터를 조회합니다.
     *
     * @param routeId 경로 ID
     * @return 경로 데이터 (없으면 null)
     */
    public RouteResult getRouteData(String routeId) {
        String dataKey = DATA_KEY_PREFIX + routeId;
        String json = redisTemplate.opsForValue().get(dataKey);

        if (json == null) {
            log.warn("캐시된 경로 데이터를 찾을 수 없음: routeId={}", routeId);
            return null;
        }

        try {
            return objectMapper.readValue(json, RouteResult.class);
        } catch (JsonProcessingException e) {
            log.error("경로 데이터 역직렬화 실패: routeId={}", routeId, e);
            return null;
        }
    }

    /**
     * 캐싱된 경로 정보를 담는 내부 클래스
     */
    @Getter
    @AllArgsConstructor
    public static class CachedRouteInfo {
        private String routeId;
        private double startLat;
        private double startLon;
        private double distanceToUser; // 미터 단위
    }
}

