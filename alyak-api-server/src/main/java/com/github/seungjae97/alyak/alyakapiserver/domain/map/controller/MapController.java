package com.github.seungjae97.alyak.alyakapiserver.domain.map.controller;

import com.github.seungjae97.alyak.alyakapiserver.domain.map.dto.request.RouteRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.map.dto.response.RouteResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.map.service.NavigationService;
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

    private final NavigationService navigationService;

    @GetMapping
    @Operation(
            summary = "경로 찾기",
            description = "보행자 경로를 조회합니다. destinationId가 제공되면 Spatial Caching을 사용하여 성능을 최적화합니다."
    )
    public ResponseEntity<RouteResponse> findRoute(@ParameterObject RouteRequest routeRequest) {
        RouteResponse response = navigationService.findRoute(routeRequest);
        return ResponseEntity.ok(response);
    }
}
