package com.github.seungjae97.alyak.alyakapiserver.service;

import com.github.seungjae97.alyak.alyakapiserver.entity.FamilyMember;
import com.github.seungjae97.alyak.alyakapiserver.repository.FamilyMemberRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class FamilyMemberServiceImpl implements FamilyMemberService {
    private final FamilyMemberRepository familyMemberRepository;

    public FamilyMemberServiceImpl(FamilyMemberRepository familyMemberRepository) {
        this.familyMemberRepository = familyMemberRepository;
    }

    @Override
    public List<FamilyMember> getAll() {
        return familyMemberRepository.findAll();
    }

    @Override
    public Optional<FamilyMember> getById(Long id) {
        return familyMemberRepository.findById(id);
    }

    @Override
    public FamilyMember create(FamilyMember familyMember) {
        return familyMemberRepository.save(familyMember);
    }

    @Override
    public FamilyMember update(FamilyMember familyMember) {
        return familyMemberRepository.save(familyMember);
    }

    @Override
    public void delete(Long id) {
        familyMemberRepository.deleteById(id);
    }

    @Override
    public List<FamilyMember> findByFamilyId(Long familyId) {
        return familyMemberRepository.findByFamilyId(familyId);
    }

    @Override
    public List<FamilyMember> findByUserId(Long userId) {
        return familyMemberRepository.findByUserId(userId);
    }
} 