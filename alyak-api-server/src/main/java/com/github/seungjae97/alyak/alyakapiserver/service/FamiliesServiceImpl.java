package com.github.seungjae97.alyak.alyakapiserver.service;

import com.github.seungjae97.alyak.alyakapiserver.repository.FamiliesRepository;
import com.github.seungjae97.alyak.alyakapiserver.dto.FamiliesDto;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FamiliesServiceImpl implements FamiliesService {
    private final FamiliesRepository familiesRepository;

    public FamiliesServiceImpl(FamiliesRepository familiesRepository) {
        this.familiesRepository = familiesRepository;
    }

    @Override
    public List<FamiliesDto> getAll() {
        return familiesRepository.selectAll();
    }

    @Override
    public FamiliesDto getById(Long id) {
        return familiesRepository.selectById(id);
    }

    @Override
    public void create(FamiliesDto dto) {
        familiesRepository.insert(dto);
    }

    @Override
    public void update(FamiliesDto dto) {
        familiesRepository.update(dto);
    }

    @Override
    public void delete(Long id) {
        familiesRepository.delete(id);
    }
} 