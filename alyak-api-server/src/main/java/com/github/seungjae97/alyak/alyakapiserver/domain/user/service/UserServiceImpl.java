package com.github.seungjae97.alyak.alyakapiserver.domain.user.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.user.dto.PasswordUpdateRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.ProviderRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.UserRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.UserRoleRepository;
import com.github.seungjae97.alyak.alyakapiserver.global.common.exception.BusinessError;
import com.github.seungjae97.alyak.alyakapiserver.global.common.exception.BusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ProviderRepository providerRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public User updatePassword(Long id, PasswordUpdateRequest request) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        if (request.newPassword() == null || request.newPassword().isBlank()) {
            throw new BusinessException(BusinessError.NEW_PASSWORD_REQUIRED);
        }

        User updated = existingUser.toBuilder()
                .password(passwordEncoder.encode(request.newPassword().trim()))
                .build();
        return userRepository.save(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // TODO : userId가 외래키로 사용되기 때문에 일괄적으로
        providerRepository.deleteByUser_UserId(id);
        userRoleRepository.deleteByUser_UserId(id);
        userRepository.deleteById(id);
    }

}