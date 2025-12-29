package com.github.seungjae97.alyak.alyakapiserver.domain.pill.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.pill.dto.request.PillSearchRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.dto.response.PillDetailResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.dto.response.SimplePillInfo;

import java.util.List;
import java.util.Optional;

public interface PillRepositoryCustom {
    List<SimplePillInfo> searchAppearance(PillSearchRequest pillSearchRequest);
    List<SimplePillInfo> findByPillNameWithType(String pillName);
    Optional<PillDetailResponse> detailPill(Long pillId);
}
