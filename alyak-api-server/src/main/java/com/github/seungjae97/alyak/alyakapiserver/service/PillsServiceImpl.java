package com.github.seungjae97.alyak.alyakapiserver.service;

import com.github.seungjae97.alyak.alyakapiserver.repository.PillsMapper;
import com.github.seungjae97.alyak.alyakapiserver.dto.PillsDto;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PillsServiceImpl implements PillsService {
    private final PillsMapper pillsMapper;

    public PillsServiceImpl(PillsMapper pillsMapper) {
        this.pillsMapper = pillsMapper;
    }

    @Override
    public List<PillsDto> getAll() {
        return pillsMapper.selectAll();
    }

    @Override
    public PillsDto getById(Long id) {
        return pillsMapper.selectById(id);
    }

    @Override
    public void create(PillsDto dto) {
        pillsMapper.insert(dto);
    }

    @Override
    public void update(PillsDto dto) {
        pillsMapper.update(dto);
    }

    @Override
    public void delete(Long id) {
        pillsMapper.delete(id);
    }
} 