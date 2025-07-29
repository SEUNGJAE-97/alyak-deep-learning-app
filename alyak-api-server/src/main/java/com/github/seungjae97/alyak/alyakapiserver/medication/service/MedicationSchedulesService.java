package com.github.seungjae97.alyak.alyakapiserver.medication.service;

import com.github.seungjae97.alyak.alyakapiserver.medication.entity.MedicationSchedules;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MedicationSchedulesService {
    List<MedicationSchedules> getAll();
    List<MedicationSchedules> getByUserMedicationId(Long userMedicationId);
    List<MedicationSchedules> getByScheduledTimeBetween(LocalDateTime start, LocalDateTime end);
    Optional<MedicationSchedules> getById(Long id);
    MedicationSchedules create(MedicationSchedules medicationSchedule);
    MedicationSchedules update(MedicationSchedules medicationSchedule);
    void delete(Long id);
} 