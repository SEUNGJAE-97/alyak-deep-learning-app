package com.github.seungjae97.alyak.alyakapiserver.domain.pill.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.pill.dto.request.PillSearchRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.dto.response.SimplePillInfo;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.PillAppearance;

import java.util.List;

public interface PillService {

    /**
     * DB에 존재하지 않는 알약의 경우에는 외부 API 호출해서 DB에
     * 저장 후 사용자에게 알약 정보 반환
     * @param pillName 알약 이름
     * */
    void findPill(String pillName);

    /**
     * 외형, 색상, 성상, 분할선을 기준으로 검색한다.
     * @param pillSearchRequest 외형, 색상, 성상, 분할선을 필드로 갖는 dto
     * */
    List<SimplePillInfo> searchPill(PillSearchRequest pillSearchRequest);
}
