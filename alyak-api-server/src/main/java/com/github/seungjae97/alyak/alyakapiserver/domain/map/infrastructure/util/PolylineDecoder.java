package com.github.seungjae97.alyak.alyakapiserver.domain.map.infrastructure.util;

import com.github.seungjae97.alyak.alyakapiserver.domain.map.dto.common.Location;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Google Polyline Algorithm을 사용한 폴리라인 디코딩 유틸리티
 * Valhalla API의 shape 필드를 디코딩하여 좌표 리스트로 변환합니다.
 */
@Slf4j
public class PolylineDecoder {

    private static final double PRECISION = 1e5;

    /**
     * 인코딩된 폴리라인 문자열을 디코딩하여 Location 리스트로 변환합니다.
     *
     * @param encoded 인코딩된 폴리라인 문자열
     * @return 디코딩된 좌표 리스트
     */
    public static List<Location> decode(String encoded) {
        if (encoded == null || encoded.isEmpty()) {
            log.warn("빈 폴리라인 문자열이 전달되었습니다.");
            return new ArrayList<>();
        }

        List<Location> coordinates = new ArrayList<>();
        int index = 0;
        int lat = 0;
        int lon = 0;

        while (index < encoded.length()) {
            int shift = 0;
            int result = 0;
            int b;

            // 위도 디코딩
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int deltaLat = ((result & 1) != 0) ? ~(result >> 1) : (result >> 1);
            lat += deltaLat;

            shift = 0;
            result = 0;

            // 경도 디코딩
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int deltaLon = ((result & 1) != 0) ? ~(result >> 1) : (result >> 1);
            lon += deltaLon;

            // 정밀도로 나누어 실제 좌표로 변환
            double latitude = lat / PRECISION;
            double longitude = lon / PRECISION;

            coordinates.add(new Location(latitude, longitude));
        }

        return coordinates;
    }
}

