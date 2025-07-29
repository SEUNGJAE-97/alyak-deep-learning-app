package com.github.seungjae97.alyak.alyakapiserver.medication.repository;

import com.github.seungjae97.alyak.alyakapiserver.medication.entity.MedicationSchedules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MedicationSchedulesRepository extends JpaRepository<MedicationSchedules, Long> {
    List<MedicationSchedules> findByUserMedicationId(Long userMedicationId);
    List<MedicationSchedules> findByScheduledTimeBetween(LocalDateTime start, LocalDateTime end);
} 