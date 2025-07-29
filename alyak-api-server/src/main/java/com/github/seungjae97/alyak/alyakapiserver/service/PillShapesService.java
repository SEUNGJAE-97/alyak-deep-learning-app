package com.github.seungjae97.alyak.alyakapiserver.service;

import com.github.seungjae97.alyak.alyakapiserver.entity.PillShapes;
import java.util.List;
import java.util.Optional;

public interface PillShapesService {
    List<PillShapes> getAll();
    Optional<PillShapes> getById(Long id);
    PillShapes create(PillShapes pillShapes);
    PillShapes update(PillShapes pillShapes);
    void delete(Long id);
} 