package com.github.seungjae97.alyak.alyakapiserver.service;

import com.github.seungjae97.alyak.alyakapiserver.entity.Pills;
import java.util.List;
import java.util.Optional;

public interface PillsService {
    List<Pills> getAll();
    Optional<Pills> getById(Long id);
    Pills create(Pills pills);
    Pills update(Pills pills);
    void delete(Long id);
    List<Pills> findByPillShapeId(Long pillShapeId);
    List<Pills> findByManufacturer(String manufacturer);
} 