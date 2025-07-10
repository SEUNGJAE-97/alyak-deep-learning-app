package com.github.seungjae97.alyak.alyakapiserver.service;

import com.github.seungjae97.alyak.alyakapiserver.dto.FamiliesDto;
import java.util.List;

public interface FamiliesService {
    List<FamiliesDto> getAll();
    FamiliesDto getById(Long id);
    void create(FamiliesDto dto);
    void update(FamiliesDto dto);
    void delete(Long id);
} 