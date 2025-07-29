package com.github.seungjae97.alyak.alyakapiserver.service;

import com.github.seungjae97.alyak.alyakapiserver.entity.Pills;
import com.github.seungjae97.alyak.alyakapiserver.repository.PillsRepository;
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
    public Optional<Pills> getById(Long id) {
        return pillsRepository.findById(id);
    }

    @Override
    public Pills create(Pills pills) {
        return pillsRepository.save(pills);
    }

    @Override
    public Pills update(Pills pills) {
        return pillsRepository.save(pills);
    }

    @Override
    public void delete(Long id) {
        pillsRepository.deleteById(id);
    }

    @Override
    public List<Pills> findByPillShapeId(Long pillShapeId) {
        return pillsRepository.findByPillShapeId(pillShapeId);
    }

    @Override
    public List<Pills> findByManufacturer(String manufacturer) {
        return pillsRepository.findByManufacturer(manufacturer);
    }
} 