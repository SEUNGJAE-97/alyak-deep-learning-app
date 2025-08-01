package com.github.seungjae97.alyak.alyakapiserver.medication.repository;

import com.github.seungjae97.alyak.alyakapiserver.medication.entity.MedicationSchedules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MedicationSchedulesRepository extends JpaRepository<MedicationSchedules, Long> {
    List<MedicationSchedules> findByUserMedicationId(Long userMedicationId);
    List<MedicationSchedules> findByScheduledTimeBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * 특정 사용자의 모든 알약 스케줄 조회
     * @param userId 사용자 ID
     * @return 해당 사용자의 알약 스케줄 목록
     */
    @Query("SELECT ms FROM MedicationSchedules ms JOIN ms.userMedication um WHERE um.user.id = :userId")
    List<MedicationSchedules> findByUserId(@Param("userId") Long userId);
} 