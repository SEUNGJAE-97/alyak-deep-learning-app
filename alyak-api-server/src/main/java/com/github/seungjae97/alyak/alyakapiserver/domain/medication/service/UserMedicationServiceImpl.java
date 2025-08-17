package com.github.seungjae97.alyak.alyakapiserver.domain.medication.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.medication.entity.UserMedication;
import com.github.seungjae97.alyak.alyakapiserver.domain.medication.repository.UserMedicationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserMedicationServiceImpl implements UserMedicationService {
    
    private final UserMedicationRepository userMedicationRepository;
    
    public UserMedicationServiceImpl(UserMedicationRepository userMedicationRepository) {
        this.userMedicationRepository = userMedicationRepository;
    }
    
    @Override
    public List<UserMedication> getAll() {
        return userMedicationRepository.findAll();
    }
    
    @Override
    public List<UserMedication> getByUserId(Long userId) {
        return userMedicationRepository.findByUserId(userId);
    }
    
    @Override
    public Optional<UserMedication> getById(Long id) {
        return userMedicationRepository.findById(id);
    }
    
    @Override
    public UserMedication create(UserMedication userMedication) {
        return userMedicationRepository.save(userMedication);
    }
    
    @Override
    public UserMedication update(UserMedication userMedication) {
        return userMedicationRepository.save(userMedication);
    }
    
    @Override
    public void delete(Long id) {
        userMedicationRepository.deleteById(id);
    }
} 