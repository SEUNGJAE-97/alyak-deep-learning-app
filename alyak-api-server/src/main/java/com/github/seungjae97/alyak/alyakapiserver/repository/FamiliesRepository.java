package com.github.seungjae97.alyak.alyakapiserver.repository;

import com.github.seungjae97.alyak.alyakapiserver.dto.FamiliesDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FamiliesRepository {
    List<FamiliesDto> selectAll();
    FamiliesDto selectById(Long id);
    int insert(FamiliesDto dto);
    int update(FamiliesDto dto);
    int delete(Long id);
} 