package com.github.seungjae97.alyak.alyakapiserver.domain.family.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.family.entity.FamilyMember;

import java.util.List;
import java.util.Optional;

public interface FamilyMemberService {
    /**
     * 특정 가족의 모든 구성원 조회
     * @param familyId 가족 식별자
     * @return 해당 가족의 구성원 목록
     */
    List<FamilyMember> getByFamilyId(Long familyId);
    
    /**
     * 특정 가족 구성원 조회
     * @param id 가족 구성원 식별자
     * @return 가족 구성원 정보
     */
    Optional<FamilyMember> getById(Long id);
    
    /**
     * 가족 구성원 생성
     * @param familyMember 생성할 가족 구성원 정보
     * @return 생성된 가족 구성원
     */
    FamilyMember create(FamilyMember familyMember);
    
    /**
     * 가족 구성원 정보 수정
     * @param familyMember 수정할 가족 구성원 정보
     * @return 수정된 가족 구성원
     */
    FamilyMember update(FamilyMember familyMember);
    
    /**
     * 가족 구성원 삭제
     * @param id 삭제할 가족 구성원 식별자
     */
    void delete(Long id);
} 