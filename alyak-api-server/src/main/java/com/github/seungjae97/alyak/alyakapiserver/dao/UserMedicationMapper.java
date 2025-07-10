package com.github.seungjae97.alyak.alyakapiserver.dao;

import com.github.seungjae97.alyak.alyakapiserver.dto.UserMedicationDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface UserMedicationMapper {
    List<UserMedicationDto> selectAll();
    UserMedicationDto selectById(Long id);
    int insert(UserMedicationDto dto);
    int update(UserMedicationDto dto);
    int delete(Long id);
} 