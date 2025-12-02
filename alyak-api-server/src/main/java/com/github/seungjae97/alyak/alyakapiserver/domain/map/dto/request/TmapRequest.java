package com.github.seungjae97.alyak.alyakapiserver.domain.map.dto.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TmapRequest {
    private double startX;          // 출발지 경도
    private double startY;          // 출발지 위도
    private double endX;            // 도착지 경도
    private double endY;            // 도착지 위도
    private String startName;       // 출발지 명칭
    private String endName;         // 도착지 명칭

    @Builder.Default
    private String reqCoordType = "WGS84GEO"; // 중요: 요청 좌표계
    @Builder.Default
    private String resCoordType = "WGS84GEO"; // 중요: 응답 좌표계
    @Builder.Default
    private String searchOption = "0";        // 0: 추천 경로
}
