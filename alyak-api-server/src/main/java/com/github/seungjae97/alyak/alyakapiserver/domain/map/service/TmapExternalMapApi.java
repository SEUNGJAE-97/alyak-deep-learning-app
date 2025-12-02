package com.github.seungjae97.alyak.alyakapiserver.domain.map.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.seungjae97.alyak.alyakapiserver.domain.map.dto.common.Location;
import com.github.seungjae97.alyak.alyakapiserver.domain.map.dto.internal.RouteResult;
import com.github.seungjae97.alyak.alyakapiserver.domain.map.dto.request.RouteRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.map.dto.request.TmapRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Tmap API를 사용하는 ExternalMapApi 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TmapExternalMapApi implements ExternalMapApi {

    @Value("${TMAP_API_KEY}")
    private String tmapApiKey;

    private final ObjectMapper objectMapper;

    @Override
    public RouteResult getRoute(RouteRequest request) {
        TmapRequest tmapRequest = TmapRequest.builder()
                .startX(request.getStartLng())
                .startY(request.getStartLat())
                .endX(request.getEndLng())
                .endY(request.getEndLat())
                .startName("Start")
                .endName("End")
                .reqCoordType("WGS84GEO")
                .resCoordType("WGS84GEO")
                .build();

        // Tmap API 호출
        WebClient client = WebClient.create("https://apis.openapi.sk.com");
        String responseJson = client.post()
                .uri("/tmap/routes/pedestrian?version=1")
                .header("appKey", tmapApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(tmapRequest)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // Tmap 응답 파싱
        return parseTmapResponse(responseJson);
    }

    private RouteResult parseTmapResponse(String jsonString) {
        List<Location> path = new ArrayList<>();
        int totalDistance = 0;
        int totalTime = 0;

        try {
            JsonNode rootNode = objectMapper.readTree(jsonString);
            JsonNode features = rootNode.path("features");

            if (features.isArray()) {
                for (JsonNode feature : features) {
                    JsonNode geometry = feature.path("geometry");
                    String type = geometry.path("type").asText();

                    // "LineString" 타입만 추출 (실제 경로 선)
                    if ("LineString".equals(type)) {
                        JsonNode coordinates = geometry.path("coordinates");

                        for (JsonNode point : coordinates) {
                            // GeoJSON은 [경도(x), 위도(y)] 순서
                            double lng = point.get(0).asDouble();
                            double lat = point.get(1).asDouble();
                            path.add(new Location(lat, lng));
                        }
                    }

                    // Properties에서 거리/시간 정보 추출 (Tmap 응답 구조에 따라 조정 필요)
                    JsonNode properties = feature.path("properties");
                    if (!properties.isMissingNode()) {
                        JsonNode totalDistanceNode = properties.path("totalDistance");
                        JsonNode totalTimeNode = properties.path("totalTime");

                        if (!totalDistanceNode.isMissingNode()) {
                            totalDistance = totalDistanceNode.asInt();
                        }
                        if (!totalTimeNode.isMissingNode()) {
                            totalTime = totalTimeNode.asInt();
                        }
                    }
                }
            }

            // Tmap 응답에서 전체 요약 정보 확인
            JsonNode properties = rootNode.path("properties");
            if (!properties.isMissingNode()) {
                JsonNode totalDistanceNode = properties.path("totalDistance");
                JsonNode totalTimeNode = properties.path("totalTime");

                if (!totalDistanceNode.isMissingNode()) {
                    totalDistance = totalDistanceNode.asInt();
                }
                if (!totalTimeNode.isMissingNode()) {
                    totalTime = totalTimeNode.asInt();
                }
            }

        } catch (Exception e) {
            log.error("Tmap 데이터 파싱 실패", e);
            throw new RuntimeException("Tmap 데이터 파싱 실패", e);
        }

        return RouteResult.builder()
                .path(path)
                .totalDistance(totalDistance)
                .totalTime(totalTime)
                .build();
    }
}

