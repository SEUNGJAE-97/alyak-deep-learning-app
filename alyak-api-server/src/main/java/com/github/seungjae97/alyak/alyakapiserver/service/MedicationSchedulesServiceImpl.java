package com.github.seungjae97.alyak.alyakapiserver.service;

import com.github.seungjae97.alyak.alyakapiserver.dao.MedicationSchedulesMapper;
import com.github.seungjae97.alyak.alyakapiserver.dto.MedicationSchedulesDto;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MedicationSchedulesServiceImpl implements MedicationSchedulesService {
    private final MedicationSchedulesMapper medicationSchedulesMapper;

    public MedicationSchedulesServiceImpl(MedicationSchedulesMapper medicationSchedulesMapper) {
        this.medicationSchedulesMapper = medicationSchedulesMapper;
    }

    @Override
    public List<MedicationSchedulesDto> getAll() {
        return medicationSchedulesMapper.selectAll();
    }

    @Override
    public MedicationSchedulesDto getById(Long id) {
        return medicationSchedulesMapper.selectById(id);
    }

    @Override
    public void create(MedicationSchedulesDto dto) {
        medicationSchedulesMapper.insert(dto);
    }

    @Override
    public void update(MedicationSchedulesDto dto) {
        medicationSchedulesMapper.update(dto);
    }

    @Override
    public void delete(Long id) {
        medicationSchedulesMapper.delete(id);
    }
} 