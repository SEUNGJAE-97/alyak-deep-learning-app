package com.github.seungjae97.alyak.alyakapiserver.service;

import com.github.seungjae97.alyak.alyakapiserver.repository.FamilyMemberMapper;
import com.github.seungjae97.alyak.alyakapiserver.dto.FamilyMemberDto;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FamilyMemberServiceImpl implements FamilyMemberService {
    private final FamilyMemberMapper familyMemberMapper;

    public FamilyMemberServiceImpl(FamilyMemberMapper familyMemberMapper) {
        this.familyMemberMapper = familyMemberMapper;
    }

    @Override
    public List<FamilyMemberDto> getAll() {
        return familyMemberMapper.selectAll();
    }

    @Override
    public FamilyMemberDto getById(Long id) {
        return familyMemberMapper.selectById(id);
    }

    @Override
    public void create(FamilyMemberDto dto) {
        familyMemberMapper.insert(dto);
    }

    @Override
    public void update(FamilyMemberDto dto) {
        familyMemberMapper.update(dto);
    }

    @Override
    public void delete(Long id) {
        familyMemberMapper.delete(id);
    }
} 