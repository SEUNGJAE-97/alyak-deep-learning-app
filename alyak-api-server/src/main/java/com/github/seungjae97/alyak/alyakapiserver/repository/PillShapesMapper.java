package com.github.seungjae97.alyak.alyakapiserver.repository;

import com.github.seungjae97.alyak.alyakapiserver.dto.PillShapesDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface PillShapesMapper {
    List<PillShapesDto> selectAll();
    PillShapesDto selectById(Long id);
    int insert(PillShapesDto dto);
    int update(PillShapesDto dto);
    int delete(Long id);
} 