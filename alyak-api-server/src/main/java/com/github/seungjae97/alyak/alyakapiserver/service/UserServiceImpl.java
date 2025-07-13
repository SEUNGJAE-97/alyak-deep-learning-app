package com.github.seungjae97.alyak.alyakapiserver.service;

import com.github.seungjae97.alyak.alyakapiserver.repository.UserMapper;
import com.github.seungjae97.alyak.alyakapiserver.dto.UserDto;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public List<UserDto> getAll() {
        return userMapper.selectAll();
    }

    @Override
    public UserDto getById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public void create(UserDto dto) {
        userMapper.insert(dto);
    }

    @Override
    public void update(UserDto dto) {
        userMapper.update(dto);
    }

    @Override
    public void delete(Long id) {
        userMapper.delete(id);
    }
} 