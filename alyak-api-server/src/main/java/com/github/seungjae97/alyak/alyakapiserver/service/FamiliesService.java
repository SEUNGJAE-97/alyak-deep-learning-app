package com.github.seungjae97.alyak.alyakapiserver.service;

import com.github.seungjae97.alyak.alyakapiserver.entity.Families;
import java.util.List;
import java.util.Optional;

public interface FamiliesService {
    List<Families> getAll();
    Optional<Families> getById(Long id);
    Families create(Families families);
    Families update(Families families);
    void delete(Long id);
} 