package com.github.seungjae97.alyak.alyakapiserver.domain.family.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.family.entity.Family;

import java.util.List;
import java.util.Optional;

public interface FamiliesService {
    /**
     * 모든 가족 정보 조회
     * */
    List<Family> getAll();
    /**
     * @param familyId : familyId에 해당하는 Families 반환
     * */
    Optional<Family> getById(Long familyId);
    /**
     * family 생성
     * @param family : 가족
     * */
    Family createFamily(Family family);
    /**
     * 가족 정보 갱신
     * */
    Family updateFamily(Family family);
    /**
     * familyId로 가족 정보 삭제
     * @param familyId : 가족 id
     * */
    void deleteFamily(Long familyId);
} 