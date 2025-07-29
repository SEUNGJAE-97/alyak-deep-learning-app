package com.github.seungjae97.alyak.alyakapiserver.service;

import com.github.seungjae97.alyak.alyakapiserver.entity.PillShapes;
import com.github.seungjae97.alyak.alyakapiserver.repository.PillShapesRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PillShapesServiceImpl implements PillShapesService {
    private final PillShapesRepository pillShapesRepository;

    public PillShapesServiceImpl(PillShapesRepository pillShapesRepository) {
        this.pillShapesRepository = pillShapesRepository;
    }

    @Override
    public List<PillShapes> getAll() {
        return pillShapesRepository.findAll();
    }

    @Override
    public Optional<PillShapes> getById(Long id) {
        return pillShapesRepository.findById(id);
    }

    @Override
    public PillShapes create(PillShapes pillShapes) {
        return pillShapesRepository.save(pillShapes);
    }

    @Override
    public PillShapes update(PillShapes pillShapes) {
        return pillShapesRepository.save(pillShapes);
    }

    @Override
    public void delete(Long id) {
        pillShapesRepository.deleteById(id);
    }
} 