package com.github.seungjae97.alyak.alyakapiserver.family.service;

import com.github.seungjae97.alyak.alyakapiserver.family.entity.Families;

import java.util.List;
import java.util.Optional;

public interface FamiliesService {
    List<Families> getAll();
    Optional<Families> getById(Long id);
    Families create(Families family);
    Families update(Families family);
    void delete(Long id);
} 