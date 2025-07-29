package com.github.seungjae97.alyak.alyakapiserver.family.service;

import com.github.seungjae97.alyak.alyakapiserver.family.entity.FamilyMember;

import java.util.List;
import java.util.Optional;

public interface FamilyMemberService {
    List<FamilyMember> getAll();
    List<FamilyMember> getByFamilyId(Long familyId);
    Optional<FamilyMember> getById(Long id);
    FamilyMember create(FamilyMember familyMember);
    FamilyMember update(FamilyMember familyMember);
    void delete(Long id);
} 