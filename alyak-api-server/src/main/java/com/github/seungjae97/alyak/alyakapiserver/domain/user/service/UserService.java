package com.github.seungjae97.alyak.alyakapiserver.domain.user.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAll();
    Optional<User> getById(Long id);
    Optional<User> getByEmail(String email);
    User create(User user);
    User update(User user);
    void delete(Long id);
    boolean existsByEmail(String email);
} 