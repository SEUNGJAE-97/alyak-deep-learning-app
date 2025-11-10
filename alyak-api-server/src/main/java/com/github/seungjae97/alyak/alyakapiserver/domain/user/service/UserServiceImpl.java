package com.github.seungjae97.alyak.alyakapiserver.domain.user.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.user.dto.UserUpdateRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User create(User user) {
        User encodedUser = user.toBuilder()
                .password(passwordEncoder.encode(user.getPassword()))
                .build();
        return userRepository.save(encodedUser);
    }

    @Override
    public User update(Long id, UserUpdateRequest request) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        User.UserBuilder updatedBuilder = existingUser.toBuilder();

        if (request.email() != null) {
            updatedBuilder.email(request.email());
        }
        if (request.name() != null) {
            updatedBuilder.name(request.name());
        }
        return null;
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}