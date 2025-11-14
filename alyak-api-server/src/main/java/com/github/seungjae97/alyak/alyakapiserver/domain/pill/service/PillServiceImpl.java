package com.github.seungjae97.alyak.alyakapiserver.domain.pill.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.pill.dto.request.PillSearchRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.dto.response.SimplePillInfo;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.Pill;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.PillAppearance;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.repository.PillRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PillServiceImpl implements PillService {

    private final PillRepository pillRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public void findPill(String pillName) {
        List<Pill> pillList = pillRepository.findByPillName(pillName);
        if(pillList.isEmpty()){
            // 0. 외부 API를 조회한다.

            // 1. 만약 외부 API 조회결과가 없다면?
            // 1.1 예외처리
            // 2. 만약 외부 API 조회결과가 존재한다면? 새 알약을 DB에 저장
            // 2.1 사용자에게 알약 정보 전달
        }
    }

    @Override
    public List<SimplePillInfo> searchPill(PillSearchRequest pillSearchRequest) {
        return pillRepository.findByAppearance(pillSearchRequest);
    }
}
