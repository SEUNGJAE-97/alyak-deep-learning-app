package com.github.seungjae97.alyak.alyakapiserver.dao;

import com.github.seungjae97.alyak.alyakapiserver.dto.MedicationSchedulesDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface MedicationSchedulesMapper {
    List<MedicationSchedulesDto> selectAll();
    MedicationSchedulesDto selectById(Long id);
    int insert(MedicationSchedulesDto dto);
    int update(MedicationSchedulesDto dto);
    int delete(Long id);
} 