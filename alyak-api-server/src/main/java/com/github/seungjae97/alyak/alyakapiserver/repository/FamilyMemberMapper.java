package com.github.seungjae97.alyak.alyakapiserver.dao;

import com.github.seungjae97.alyak.alyakapiserver.dto.FamilyMemberDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface FamilyMemberMapper {
    List<FamilyMemberDto> selectAll();
    FamilyMemberDto selectById(Long id);
    int insert(FamilyMemberDto dto);
    int update(FamilyMemberDto dto);
    int delete(Long id);
} 