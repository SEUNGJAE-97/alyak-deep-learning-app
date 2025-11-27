package com.github.seungjae97.alyak.alyakapiserver.domain.map.controller;

import com.github.seungjae97.alyak.alyakapiserver.domain.map.dto.request.RouteRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.map.dto.response.RouteResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.map.service.MapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
@Tag(name = "09.Map", description = "보행자 경로 관련 API")
public class MapController {

    private final MapService mapService;

    @GetMapping
    @Operation(summary = "경로 찾기", description = "보도로 출발, 목적지 경도/위도 값으로 경로를 준다.")
    public ResponseEntity<RouteResponse> findRoute(@ParameterObject RouteRequest routeRequest) {
        RouteResponse response = mapService.getRoute(routeRequest);
        return ResponseEntity.ok(response);
    }
}
