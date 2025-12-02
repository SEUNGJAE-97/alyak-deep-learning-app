package com.github.seungjae97.alyak.alyakapiserver.domain.map.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.map.dto.internal.RouteResult;
import com.github.seungjae97.alyak.alyakapiserver.domain.map.dto.request.RouteRequest;

/**
 * 외부 지도 API를 추상화하는 인터페이스
 * Tmap, Kakao 등 다양한 지도 서비스를 동일한 인터페이스로 사용할 수 있도록 합니다.
 */
public interface ExternalMapApi {

    /**
     * 외부 지도 API를 통해 전체 경로를 조회합니다.
     *
     * @param request 경로 조회 요청
     * @return 경로 결과 (RouteResult)
     */
    RouteResult getRoute(RouteRequest request);
}

