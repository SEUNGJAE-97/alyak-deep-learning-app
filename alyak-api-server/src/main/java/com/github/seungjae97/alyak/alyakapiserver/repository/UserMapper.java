package com.github.seungjae97.alyak.alyakapiserver.dao;

import com.github.seungjae97.alyak.alyakapiserver.dto.UserDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface UserMapper {
    List<UserDto> selectAll();
    UserDto selectById(Long id);
    int insert(UserDto dto);
    int update(UserDto dto);
    int delete(Long id);
} 