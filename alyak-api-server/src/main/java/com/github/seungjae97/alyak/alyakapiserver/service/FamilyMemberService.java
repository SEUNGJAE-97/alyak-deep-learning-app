package com.github.seungjae97.alyak.alyakapiserver.service;

import com.github.seungjae97.alyak.alyakapiserver.entity.FamilyMember;
import java.util.List;
import java.util.Optional;

public interface FamilyMemberService {
    List<FamilyMember> getAll();
    Optional<FamilyMember> getById(Long id);
    FamilyMember create(FamilyMember familyMember);
    FamilyMember update(FamilyMember familyMember);
    void delete(Long id);
    List<FamilyMember> findByFamilyId(Long familyId);
    List<FamilyMember> findByUserId(Long userId);
} 