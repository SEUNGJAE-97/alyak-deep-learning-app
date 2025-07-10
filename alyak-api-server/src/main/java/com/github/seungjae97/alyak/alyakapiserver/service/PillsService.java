package com.github.seungjae97.alyak.alyakapiserver.service;

import com.github.seungjae97.alyak.alyakapiserver.dto.PillsDto;
import java.util.List;

public interface PillsService {
    List<PillsDto> getAll();
    PillsDto getById(Long id);
    void create(PillsDto dto);
    void update(PillsDto dto);
    void delete(Long id);
} 