package com.github.seungjae97.alyak.alyakapiserver.service;

import com.github.seungjae97.alyak.alyakapiserver.dao.UserMedicationMapper;
import com.github.seungjae97.alyak.alyakapiserver.dto.UserMedicationDto;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserMedicationServiceImpl implements UserMedicationService {
    private final UserMedicationMapper userMedicationMapper;

    public UserMedicationServiceImpl(UserMedicationMapper userMedicationMapper) {
        this.userMedicationMapper = userMedicationMapper;
    }

    @Override
    public List<UserMedicationDto> getAll() {
        return userMedicationMapper.selectAll();
    }

    @Override
    public UserMedicationDto getById(Long id) {
        return userMedicationMapper.selectById(id);
    }

    @Override
    public void create(UserMedicationDto dto) {
        userMedicationMapper.insert(dto);
    }

    @Override
    public void update(UserMedicationDto dto) {
        userMedicationMapper.update(dto);
    }

    @Override
    public void delete(Long id) {
        userMedicationMapper.delete(id);
    }
} 