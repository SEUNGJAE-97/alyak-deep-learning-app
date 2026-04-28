package com.github.seungjae97.alyak.alyakapiserver.domain.admin.controller;

import com.github.seungjae97.alyak.alyakapiserver.domain.admin.dto.AdminSessionResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.admin.service.AdminService;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@AdminApiController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "08.Admin", description = "관리자 전용 API")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/me")
    @Operation(summary = "관리자 세션 조회", description = "현재 로그인한 관리자 정보와 권한 목록을 반환합니다.")
    public ResponseEntity<AdminSessionResponse> getAdminSession(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        AdminSessionResponse response = adminService.getCurrentAdminSession(userDetails.getUser().getUserId());
        return ResponseEntity.ok(response);
    }


}
