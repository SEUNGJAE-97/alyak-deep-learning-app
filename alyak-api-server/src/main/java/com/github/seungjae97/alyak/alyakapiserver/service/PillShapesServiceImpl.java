package com.github.seungjae97.alyak.alyakapiserver.service;

import com.github.seungjae97.alyak.alyakapiserver.repository.PillShapesMapper;
import com.github.seungjae97.alyak.alyakapiserver.dto.PillShapesDto;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PillShapesServiceImpl implements PillShapesService {
    private final PillShapesMapper pillShapesMapper;

    public PillShapesServiceImpl(PillShapesMapper pillShapesMapper) {
        this.pillShapesMapper = pillShapesMapper;
    }

    @Override
    public List<PillShapesDto> getAll() {
        return pillShapesMapper.selectAll();
    }

    @Override
    public PillShapesDto getById(Long id) {
        return pillShapesMapper.selectById(id);
    }

    @Override
    public void create(PillShapesDto dto) {
        pillShapesMapper.insert(dto);
    }

    @Override
    public void update(PillShapesDto dto) {
        pillShapesMapper.update(dto);
    }

    @Override
    public void delete(Long id) {
        pillShapesMapper.delete(id);
    }
} 