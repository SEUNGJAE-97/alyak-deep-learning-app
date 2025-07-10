package com.github.seungjae97.alyak.alyakapiserver.dao;

import com.github.seungjae97.alyak.alyakapiserver.dto.FamiliesDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FamiliesMapper {
    List<FamiliesDto> selectAll();
    FamiliesDto selectById(Long id);
    int insert(FamiliesDto dto);
    int update(FamiliesDto dto);
    int delete(Long id);
} 