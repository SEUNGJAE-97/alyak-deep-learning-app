package com.github.seungjae97.alyak.alyakapiserver.service;

import com.github.seungjae97.alyak.alyakapiserver.entity.MedicationSchedules;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

public interface MedicationSchedulesService {
    List<MedicationSchedules> getAll();
    Optional<MedicationSchedules> getById(Long id);
    MedicationSchedules create(MedicationSchedules medicationSchedules);
    MedicationSchedules update(MedicationSchedules medicationSchedules);
    void delete(Long id);
    List<MedicationSchedules> findByUserMedicationId(Long userMedicationId);
    List<MedicationSchedules> findByScheduledTimeBetween(LocalDateTime start, LocalDateTime end);
    List<MedicationSchedules> findByStatus(MedicationSchedules.Status status);
} 