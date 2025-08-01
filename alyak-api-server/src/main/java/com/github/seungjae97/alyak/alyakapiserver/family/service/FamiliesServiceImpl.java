package com.github.seungjae97.alyak.alyakapiserver.family.service;

import com.github.seungjae97.alyak.alyakapiserver.family.entity.Families;
import com.github.seungjae97.alyak.alyakapiserver.family.repository.FamiliesRepository;
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
    public List<Families> getAll() {
        return familiesRepository.findAll();
    }
    
    @Override
    public Optional<Families> getById(Long familyId) {
        return familiesRepository.findById(familyId);
    }
    
    @Override
    public Families createFamily(Families family) {
        return familiesRepository.save(family);
    }
    
    @Override
    public Families updateFamily(Families family) {
        return familiesRepository.save(family);
    }
    
    @Override
    public void deleteFamily(Long familyId) {
        familiesRepository.deleteById(familyId);
    }
} 