package com.github.seungjae97.alyak.alyakapiserver.service;

import com.github.seungjae97.alyak.alyakapiserver.dao.FamiliesMapper;
import com.github.seungjae97.alyak.alyakapiserver.dto.FamiliesDto;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FamiliesServiceImpl implements FamiliesService {
    private final FamiliesMapper familiesMapper;

    public FamiliesServiceImpl(FamiliesMapper familiesMapper) {
        this.familiesMapper = familiesMapper;
    }

    @Override
    public List<FamiliesDto> getAll() {
        return familiesMapper.selectAll();
    }

    @Override
    public FamiliesDto getById(Long id) {
        return familiesMapper.selectById(id);
    }

    @Override
    public void create(FamiliesDto dto) {
        familiesMapper.insert(dto);
    }

    @Override
    public void update(FamiliesDto dto) {
        familiesMapper.update(dto);
    }

    @Override
    public void delete(Long id) {
        familiesMapper.delete(id);
    }
} 