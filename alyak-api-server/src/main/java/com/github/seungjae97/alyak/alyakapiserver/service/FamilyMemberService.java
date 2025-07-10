package com.github.seungjae97.alyak.alyakapiserver.service;

import com.github.seungjae97.alyak.alyakapiserver.dto.FamilyMemberDto;
import java.util.List;

public interface FamilyMemberService {
    List<FamilyMemberDto> getAll();
    FamilyMemberDto getById(Long id);
    void create(FamilyMemberDto dto);
    void update(FamilyMemberDto dto);
    void delete(Long id);
} 