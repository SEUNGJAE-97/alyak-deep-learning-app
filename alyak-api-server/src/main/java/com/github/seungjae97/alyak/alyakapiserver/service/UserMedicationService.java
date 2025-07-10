package com.github.seungjae97.alyak.alyakapiserver.service;

import com.github.seungjae97.alyak.alyakapiserver.dto.UserMedicationDto;
import java.util.List;

public interface UserMedicationService {
    List<UserMedicationDto> getAll();
    UserMedicationDto getById(Long id);
    void create(UserMedicationDto dto);
    void update(UserMedicationDto dto);
    void delete(Long id);
} 