package com.github.seungjae97.alyak.alyakapiserver.domain.pill.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.Pill;
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
    public List<Pill> getAll() {
        return pillsRepository.findAll();
    }
    
    @Override
    public List<Pill> getByPillShapeId(Long pillShapeId) {
        return pillsRepository.findByPillShapeId(pillShapeId);
    }
    
    @Override
    public Optional<Pill> getById(Long id) {
        return pillsRepository.findById(id);
    }
    
    @Override
    public Pill create(Pill pill) {
        return pillsRepository.save(pill);
    }
    
    @Override
    public Pill update(Pill pill) {
        return pillsRepository.save(pill);
    }
    
    @Override
    public void delete(Long id) {
        pillsRepository.deleteById(id);
    }
} 