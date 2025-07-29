package com.github.seungjae97.alyak.alyakapiserver.pill.service;

import com.github.seungjae97.alyak.alyakapiserver.pill.entity.Pills;

import java.util.List;
import java.util.Optional;

public interface PillsService {
    List<Pills> getAll();
    List<Pills> getByPillShapeId(Long pillShapeId);
    Optional<Pills> getById(Long id);
    Pills create(Pills pill);
    Pills update(Pills pill);
    void delete(Long id);
} 