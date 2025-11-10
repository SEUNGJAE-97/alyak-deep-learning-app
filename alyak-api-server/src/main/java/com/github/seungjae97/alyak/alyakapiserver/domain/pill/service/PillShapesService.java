package com.github.seungjae97.alyak.alyakapiserver.domain.pill.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.PillShape;

import java.util.List;
import java.util.Optional;

public interface PillShapesService {
    List<PillShape> getAll();
    Optional<PillShape> getById(Long id);
    PillShape create(PillShape pillShape);
    PillShape update(PillShape pillShape);
    void delete(Long id);
} 