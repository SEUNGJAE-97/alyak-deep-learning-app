package com.github.seungjae97.alyak.alyakapiserver.medication.service;

import com.github.seungjae97.alyak.alyakapiserver.medication.entity.MedicationSchedules;
import com.github.seungjae97.alyak.alyakapiserver.medication.repository.MedicationSchedulesRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MedicationSchedulesServiceImpl implements MedicationSchedulesService {
    
    private final MedicationSchedulesRepository medicationSchedulesRepository;
    
    public MedicationSchedulesServiceImpl(MedicationSchedulesRepository medicationSchedulesRepository) {
        this.medicationSchedulesRepository = medicationSchedulesRepository;
    }
    
    @Override
    public List<MedicationSchedules> getByUserId(Long userId) {
        return medicationSchedulesRepository.findByUserId(userId);
    }
    
    @Override
    public List<MedicationSchedules> getByUserMedicationId(Long userMedicationId) {
        return medicationSchedulesRepository.findByUserMedicationId(userMedicationId);
    }
    
    @Override
    public List<MedicationSchedules> getByScheduledTimeBetween(LocalDateTime start, LocalDateTime end) {
        return medicationSchedulesRepository.findByScheduledTimeBetween(start, end);
    }
    
    @Override
    public Optional<MedicationSchedules> getById(Long id) {
        return medicationSchedulesRepository.findById(id);
    }
    
    @Override
    public MedicationSchedules create(MedicationSchedules medicationSchedule) {
        return medicationSchedulesRepository.save(medicationSchedule);
    }
    
    @Override
    public MedicationSchedules update(MedicationSchedules medicationSchedule) {
        return medicationSchedulesRepository.save(medicationSchedule);
    }
    
    @Override
    public void delete(Long id) {
        medicationSchedulesRepository.deleteById(id);
    }
} 