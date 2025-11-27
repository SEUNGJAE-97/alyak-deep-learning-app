package com.github.seungjae97.alyak.alyakapiserver.domain.map.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.seungjae97.alyak.alyakapiserver.domain.map.dto.request.RouteRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.map.dto.request.TmapRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.map.dto.response.RouteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MapService {

    @Value("${TMAP_API_KEY}")
    private String tmapApiKey;

    private final ObjectMapper objectMapper;

    // 앱에서 받은 요청을 처리
    public RouteResponse getRoute(RouteRequest appRequest) {

        TmapRequest tmapRequest = TmapRequest.builder()
                .startX(appRequest.getStartLng()) // 경도 (X)
                .startY(appRequest.getStartLat()) // 위도 (Y)
                .endX(appRequest.getEndLng())
                .endY(appRequest.getEndLat())
                .startName("Start")
                .endName("End")
                .reqCoordType("WGS84GEO")
                .resCoordType("WGS84GEO")
                .build();

        // 2. TMAP API 호출
        WebClient client = WebClient.create("https://apis.openapi.sk.com");

        String responseJson = client.post()
                .uri("/tmap/routes/pedestrian?version=1")
                .header("appKey", tmapApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(tmapRequest) 
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // 3. 파싱
        List<RouteResponse.GeoPoint> points = parseTmapResponse(responseJson);
        return new RouteResponse(points);
    }

    private List<RouteResponse.GeoPoint> parseTmapResponse(String jsonString) {
        List<RouteResponse.GeoPoint> routePoints = new ArrayList<>();

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

                            // 앱으로 보낼 때는 위도, 경도 순서로 객체 생성
                            routePoints.add(new RouteResponse.GeoPoint(lat, lng));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // 로깅 처리 필요
            throw new RuntimeException("TMAP 데이터 파싱 실패");
        }

        return routePoints;
    }

}
