package com.github.seungjae97.alyak.alyakapiserver.domain.family.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.family.entity.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long> {
    List<FamilyMember> findByFamilyId(Long familyId);
} 