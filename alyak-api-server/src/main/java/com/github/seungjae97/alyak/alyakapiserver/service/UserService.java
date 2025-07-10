package com.github.seungjae97.alyak.alyakapiserver.service;

import com.github.seungjae97.alyak.alyakapiserver.dto.UserDto;
import java.util.List;

public interface UserService {
    List<UserDto> getAll();
    UserDto getById(Long id);
    void create(UserDto dto);
    void update(UserDto dto);
    void delete(Long id);
} 