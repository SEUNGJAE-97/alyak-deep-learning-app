package com.github.seungjae97.alyak.alyakapiserver.domain.admin.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.admin.dto.AdminSessionResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.UserRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.UserRoleRepository;
import com.github.seungjae97.alyak.alyakapiserver.global.common.exception.BusinessError;
import com.github.seungjae97.alyak.alyakapiserver.global.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public AdminSessionResponse getCurrentAdminSession(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(BusinessError.USER_NOT_EXIST));

        List<String> roles = userRoleRepository.findByUser_userId(userId).stream()
                .map(userRole -> userRole.getRole().getName())
                .toList();

        return AdminSessionResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .roles(roles)
                .build();
    }
}
