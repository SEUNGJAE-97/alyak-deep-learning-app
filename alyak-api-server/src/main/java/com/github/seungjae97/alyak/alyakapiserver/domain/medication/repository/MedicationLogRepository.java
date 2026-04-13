package com.github.seungjae97.alyak.alyakapiserver.domain.medication.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.medication.entity.MedicationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MedicationLogRepository extends JpaRepository<MedicationLog, Long> {

    List<MedicationLog> findByUser_UserId(Long userId);

    List<MedicationLog> findByUser_UserIdAndScheduledTimeBetween(
            Long userId,
            LocalDateTime startInclusive,
            LocalDateTime endInclusive
    );

    @Query("SELECT m FROM MedicationLog m JOIN m.user u JOIN u.familyMembers fm WHERE fm.family.id = :familyId")
    List<MedicationLog> findByFamilyId(@Param("familyId") Long familyId);
}
