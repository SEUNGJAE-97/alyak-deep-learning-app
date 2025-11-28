package com.github.seungjae97.alyak.alyakapiserver.domain.map.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Valhalla API를 호출하는 Feign Client
 */
@FeignClient(name = "valhalla-client", url = "${valhalla.host:http://localhost:8002}")
public interface ValhallaClient {

    /**
     * Valhalla 경로 조회 API 호출
     *
     * @param jsonRequest JSON 형식의 요청 문자열
     *                    예: {"locations":[{"lat":37.5547,"lon":126.9706},{"lat":37.5512,"lon":126.9882}],"costing":"pedestrian"}
     * @return Valhalla API 응답 JSON 문자열
     */
    @GetMapping(value = "/route")
    String getRoute(@RequestParam("json") String jsonRequest);
}

