package com.github.seungjae97.alyak.alyakapiserver.domain.admin.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.admin.dto.AdminSessionResponse;

public interface AdminService {
    AdminSessionResponse getCurrentAdminSession(Long userId);
}
