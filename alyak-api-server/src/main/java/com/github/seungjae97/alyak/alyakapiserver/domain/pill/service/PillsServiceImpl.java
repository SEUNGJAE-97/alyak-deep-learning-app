package com.github.seungjae97.alyak.alyakapiserver.domain.pill.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.Pills;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.repository.PillsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PillsServiceImpl implements PillsService {
    
    private final PillsRepository pillsRepository;
    
    public PillsServiceImpl(PillsRepository pillsRepository) {
        this.pillsRepository = pillsRepository;
    }
    
    @Override
    public List<Pills> getAll() {
        return pillsRepository.findAll();
    }
    
    @Override
    public List<Pills> getByPillShapeId(Long pillShapeId) {
        return pillsRepository.findByPillShapeId(pillShapeId);
    }
    
    @Override
    public Optional<Pills> getById(Long id) {
        return pillsRepository.findById(id);
    }
    
    @Override
    public Pills create(Pills pill) {
        return pillsRepository.save(pill);
    }
    
    @Override
    public Pills update(Pills pill) {
        return pillsRepository.save(pill);
    }
    
    @Override
    public void delete(Long id) {
        pillsRepository.deleteById(id);
    }
} 