package com.github.seungjae97.alyak.alyakapiserver.service;

import com.github.seungjae97.alyak.alyakapiserver.dto.MedicationSchedulesDto;
import java.util.List;

public interface MedicationSchedulesService {
    List<MedicationSchedulesDto> getAll();
    MedicationSchedulesDto getById(Long id);
    void create(MedicationSchedulesDto dto);
    void update(MedicationSchedulesDto dto);
    void delete(Long id);
} 