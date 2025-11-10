package com.github.seungjae97.alyak.alyakapiserver.domain.pill.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.PillShape;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.repository.PillShapesRepository;
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
    public List<PillShape> getAll() {
        return pillShapesRepository.findAll();
    }
    
    @Override
    public Optional<PillShape> getById(Long id) {
        return pillShapesRepository.findById(id);
    }
    
    @Override
    public PillShape create(PillShape pillShape) {
        return pillShapesRepository.save(pillShape);
    }
    
    @Override
    public PillShape update(PillShape pillShape) {
        return pillShapesRepository.save(pillShape);
    }
    
    @Override
    public void delete(Long id) {
        pillShapesRepository.deleteById(id);
    }
} 