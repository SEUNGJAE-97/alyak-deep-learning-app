package com.github.seungjae97.alyak.alyakapiserver.repository;

import com.github.seungjae97.alyak.alyakapiserver.dto.PillsDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface PillsMapper {
    List<PillsDto> selectAll();
    PillsDto selectById(Long id);
    int insert(PillsDto dto);
    int update(PillsDto dto);
    int delete(Long id);
} 