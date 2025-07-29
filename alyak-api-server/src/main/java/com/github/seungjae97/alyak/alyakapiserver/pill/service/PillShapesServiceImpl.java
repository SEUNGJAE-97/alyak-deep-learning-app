package com.github.seungjae97.alyak.alyakapiserver.pill.service;

import com.github.seungjae97.alyak.alyakapiserver.pill.entity.PillShapes;
import com.github.seungjae97.alyak.alyakapiserver.pill.repository.PillShapesRepository;
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
    public PillShapes create(PillShapes pillShape) {
        return pillShapesRepository.save(pillShape);
    }
    
    @Override
    public PillShapes update(PillShapes pillShape) {
        return pillShapesRepository.save(pillShape);
    }
    
    @Override
    public void delete(Long id) {
        pillShapesRepository.deleteById(id);
    }
} 