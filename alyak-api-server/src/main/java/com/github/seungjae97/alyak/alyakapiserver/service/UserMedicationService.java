package com.github.seungjae97.alyak.alyakapiserver.service;

import com.github.seungjae97.alyak.alyakapiserver.entity.UserMedication;
import java.util.List;
import java.util.Optional;

public interface UserMedicationService {
    List<UserMedication> getAll();
    Optional<UserMedication> getById(Long id);
    UserMedication create(UserMedication userMedication);
    UserMedication update(UserMedication userMedication);
    void delete(Long id);
    List<UserMedication> findByUserId(Long userId);
    List<UserMedication> findByPillId(Long pillId);
} 