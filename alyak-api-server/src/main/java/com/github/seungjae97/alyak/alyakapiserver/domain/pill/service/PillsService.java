package com.github.seungjae97.alyak.alyakapiserver.domain.pill.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.Pill;

import java.util.List;
import java.util.Optional;

public interface PillsService {
    List<Pill> getAll();
    List<Pill> getByPillShapeId(Long pillShapeId);
    Optional<Pill> getById(Long id);
    Pill create(Pill pill);
    Pill update(Pill pill);
    void delete(Long id);
} 