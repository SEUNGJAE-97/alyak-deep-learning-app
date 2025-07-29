package com.github.seungjae97.alyak.alyakapiserver.service;

import com.github.seungjae97.alyak.alyakapiserver.entity.Families;
import com.github.seungjae97.alyak.alyakapiserver.repository.FamiliesRepository;
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
    public Optional<Families> getById(Long id) {
        return familiesRepository.findById(id);
    }

    @Override
    public Families create(Families families) {
        return familiesRepository.save(families);
    }

    @Override
    public Families update(Families families) {
        return familiesRepository.save(families);
    }

    @Override
    public void delete(Long id) {
        familiesRepository.deleteById(id);
    }
} 