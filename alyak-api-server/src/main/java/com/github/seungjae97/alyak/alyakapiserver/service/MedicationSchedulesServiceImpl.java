package com.github.seungjae97.alyak.alyakapiserver.service;

import com.github.seungjae97.alyak.alyakapiserver.entity.MedicationSchedules;
import com.github.seungjae97.alyak.alyakapiserver.repository.MedicationSchedulesRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Service
public class MedicationSchedulesServiceImpl implements MedicationSchedulesService {
    private final MedicationSchedulesRepository medicationSchedulesRepository;

    public MedicationSchedulesServiceImpl(MedicationSchedulesRepository medicationSchedulesRepository) {
        this.medicationSchedulesRepository = medicationSchedulesRepository;
    }

    @Override
    public List<MedicationSchedules> getAll() {
        return medicationSchedulesRepository.findAll();
    }

    @Override
    public Optional<MedicationSchedules> getById(Long id) {
        return medicationSchedulesRepository.findById(id);
    }

    @Override
    public MedicationSchedules create(MedicationSchedules medicationSchedules) {
        return medicationSchedulesRepository.save(medicationSchedules);
    }

    @Override
    public MedicationSchedules update(MedicationSchedules medicationSchedules) {
        return medicationSchedulesRepository.save(medicationSchedules);
    }

    @Override
    public void delete(Long id) {
        medicationSchedulesRepository.deleteById(id);
    }

    @Override
    public List<MedicationSchedules> findByUserMedicationId(Long userMedicationId) {
        return medicationSchedulesRepository.findByUserMedicationId(userMedicationId);
    }

    @Override
    public List<MedicationSchedules> findByScheduledTimeBetween(LocalDateTime start, LocalDateTime end) {
        return medicationSchedulesRepository.findByScheduledTimeBetween(start, end);
    }

    @Override
    public List<MedicationSchedules> findByStatus(MedicationSchedules.Status status) {
        return medicationSchedulesRepository.findByStatus(status);
    }
} 