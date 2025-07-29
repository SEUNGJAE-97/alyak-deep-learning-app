package com.github.seungjae97.alyak.alyakapiserver.pill.service;

import com.github.seungjae97.alyak.alyakapiserver.pill.entity.PillShapes;

import java.util.List;
import java.util.Optional;

public interface PillShapesService {
    List<PillShapes> getAll();
    Optional<PillShapes> getById(Long id);
    PillShapes create(PillShapes pillShape);
    PillShapes update(PillShapes pillShape);
    void delete(Long id);
} 