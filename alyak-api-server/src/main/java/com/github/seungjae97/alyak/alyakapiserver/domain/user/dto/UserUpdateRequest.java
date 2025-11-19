package com.github.seungjae97.alyak.alyakapiserver.domain.user.dto;

import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.Provider;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.Role;

public record UserUpdateRequest(
        String email,
        String password,
        String name,
        Role role,
        Provider provider
) {
}

