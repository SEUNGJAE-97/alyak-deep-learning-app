package com.github.seungjae97.alyak.alyakapiserver.domain.medication.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.medication.dto.request.MedicationScheduleUpdateDto;
import com.github.seungjae97.alyak.alyakapiserver.domain.medication.entity.MedicationSchedule;
import com.github.seungjae97.alyak.alyakapiserver.domain.medication.entity.Status;
import com.github.seungjae97.alyak.alyakapiserver.domain.medication.repository.MedicationSchedulesRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.medication.repository.StatusRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MedicationSchedulesServiceImpl implements MedicationSchedulesService {
    
    private final MedicationSchedulesRepository medicationSchedulesRepository;
    private final StatusRepository statusRepository;
    
    public MedicationSchedulesServiceImpl(MedicationSchedulesRepository medicationSchedulesRepository, StatusRepository statusRepository) {
        this.medicationSchedulesRepository = medicationSchedulesRepository;
        this.statusRepository = statusRepository;
    }
    
    @Override
    public List<MedicationSchedule> getByUserId(Long userId) {
        return medicationSchedulesRepository.findByUserId(userId);
    }
    
    @Override
    public List<MedicationSchedule> getByUserMedicationId(Long userMedicationId) {
        return medicationSchedulesRepository.findByUserMedicationId(userMedicationId);
    }
    
    @Override
    public List<MedicationSchedule> getByScheduledTimeBetween(LocalDateTime start, LocalDateTime end) {
        return medicationSchedulesRepository.findByScheduledTimeBetween(start, end);
    }
    
    @Override
    public Optional<MedicationSchedule> getById(Long id) {
        return medicationSchedulesRepository.findById(id);
    }
    
    @Override
    public MedicationSchedule create(MedicationSchedule medicationSchedule) {
        return medicationSchedulesRepository.save(medicationSchedule);
    }
    
    @Override
    @Transactional
    public MedicationSchedule update(Long id , MedicationScheduleUpdateDto updateDto) {
        MedicationSchedule existingSchedule = medicationSchedulesRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("MedicationSchedule not found with id: " + id));

        Status newStatus = statusRepository.findById(updateDto.statusId()).orElseThrow();

        existingSchedule.updateStatus(newStatus, updateDto.scheduleStartTime());
        existingSchedule.updateDosage(updateDto.scheduleDosage());

        return existingSchedule;
    }
    
    @Override
    public void delete(Long id) {
        medicationSchedulesRepository.deleteById(id);
    }
} 