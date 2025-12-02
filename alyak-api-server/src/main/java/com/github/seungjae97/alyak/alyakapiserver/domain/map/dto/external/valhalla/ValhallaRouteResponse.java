package com.github.seungjae97.alyak.alyakapiserver.domain.map.dto.external.valhalla;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Valhalla API 원본 응답 DTO
 * Valhalla의 JSON 구조를 그대로 매핑합니다.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValhallaRouteResponse {
    private Trip trip;
    private String status_message;
    private Integer status;
    private String units;
    private String language;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Trip {
        private List<Location> locations;
        private List<Leg> legs;
        private Summary summary;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Location {
        private String type;
        private Double lat;
        private Double lon;
        private String side_of_street;
        private Integer original_index;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Leg {
        private List<Maneuver> maneuvers;
        private Summary summary;
        
        /**
         * 인코딩된 폴리라인 문자열 (Google Polyline Encoding)
         * 디코딩하여 실제 좌표 리스트를 얻을 수 있습니다.
         */
        private String shape;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Maneuver {
        private Integer type;
        private String instruction;
        private String verbal_succinct_transition_instruction;
        private String verbal_pre_transition_instruction;
        private String verbal_post_transition_instruction;
        private Double time;
        private Double length;
        private Double cost;
        private Integer begin_shape_index;
        private Integer end_shape_index;
        private Boolean rough;
        private String travel_mode;
        private String travel_type;
        private List<String> street_names;
        private Boolean verbal_multi_cue;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Summary {
        private Boolean has_time_restrictions;
        private Boolean has_toll;
        private Boolean has_highway;
        private Boolean has_ferry;
        private Double min_lat;
        private Double min_lon;
        private Double max_lat;
        private Double max_lon;
        
        /**
         * 총 소요 시간 (초 단위)
         */
        private Double time;
        
        /**
         * 총 거리 (킬로미터 단위)
         */
        private Double length;
        
        private Double cost;
    }
}

