package com.github.seungjae97.alyak.alyakapiserver.domain.medication.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.medication.entity.UserMedication;

import java.util.List;
import java.util.Optional;

public interface UserMedicationService {
    List<UserMedication> getAll();
    List<UserMedication> getByUserId(Long userId);
    Optional<UserMedication> getById(Long id);
    UserMedication create(UserMedication userMedication);
    UserMedication update(UserMedication userMedication);
    void delete(Long id);
} 