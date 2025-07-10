package com.github.seungjae97.alyak.alyakapiserver.service;

import com.github.seungjae97.alyak.alyakapiserver.dto.PillShapesDto;
import java.util.List;

public interface PillShapesService {
    List<PillShapesDto> getAll();
    PillShapesDto getById(Long id);
    void create(PillShapesDto dto);
    void update(PillShapesDto dto);
    void delete(Long id);
} 