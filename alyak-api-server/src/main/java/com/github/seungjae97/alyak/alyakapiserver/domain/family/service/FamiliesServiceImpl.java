package com.github.seungjae97.alyak.alyakapiserver.domain.family.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.family.entity.Family;
import com.github.seungjae97.alyak.alyakapiserver.domain.family.repository.FamiliesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FamiliesServiceImpl implements FamiliesService {
    
    private final FamiliesRepository familiesRepository;
    
    public FamiliesServiceImpl(FamiliesRepository familiesRepository) {
        this.familiesRepository = familiesRepository;
    }
    
    @Override
    public List<Family> getAll() {
        return familiesRepository.findAll();
    }
    
    @Override
    public Optional<Family> getById(Long familyId) {
        return familiesRepository.findById(familyId);
    }
    
    @Override
    public Family createFamily(Family family) {
        return familiesRepository.save(family);
    }
    
    @Override
    public Family updateFamily(Family family) {
        return familiesRepository.save(family);
    }
    
    @Override
    public void deleteFamily(Long familyId) {
        familiesRepository.deleteById(familyId);
    }
} 