package com.github.seungjae97.alyak.alyakapiserver.domain.user.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.user.dto.UserUpdateRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> getById(Long id);
    User update(Long id, UserUpdateRequest request);
    void delete(Long id);
} 