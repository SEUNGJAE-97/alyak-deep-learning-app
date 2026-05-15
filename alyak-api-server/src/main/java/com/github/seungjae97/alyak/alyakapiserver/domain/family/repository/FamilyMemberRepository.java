package com.github.seungjae97.alyak.alyakapiserver.domain.family.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.family.entity.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long> {
    @Query("select fm from FamilyMember fm join fetch fm.user where fm.family.id = :familyId")
    List<FamilyMember> findAllByFamilyWithUser(Long familyId);
    boolean existsByUser_UserIdAndFamily_Id(Long userId, Long familyId);
}