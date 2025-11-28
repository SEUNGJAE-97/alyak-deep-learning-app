package com.github.seungjae97.alyak.alyakapiserver.domain.map.dto.internal;

import com.github.seungjae97.alyak.alyakapiserver.domain.map.dto.common.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 내부 도메인에서 사용하는 경로 결과 DTO
 * 비즈니스 로직 및 캐시 저장에 사용됩니다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteResult {
    /**
     * 경로 좌표 리스트 (위도, 경도)
     */
    private List<Location> path;
    
    /**
     * 총 거리 (미터 단위)
     */
    private int totalDistance;
    
    /**
     * 총 소요 시간 (초 단위)
     */
    private int totalTime;
}

