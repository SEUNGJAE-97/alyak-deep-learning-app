package com.github.seungjae97.alyak.alyakapiserver.domain.family.controller;

import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.UserDetailsImpl;
import com.github.seungjae97.alyak.alyakapiserver.domain.family.dto.response.FamilyMemberInfoResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.family.service.FamilyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/family")
@RequiredArgsConstructor
@Tag(name = "06.가족", description = "가족 정보 관련 API")
public class FamilyController {

    private final FamilyService familyService;

    @GetMapping("/members")
    @Operation(summary = "구성원 검색", description = "userId 기준 가족 구성원 조회")
    public ResponseEntity<List<FamilyMemberInfoResponse>> findMembers(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<FamilyMemberInfoResponse> members = familyService.findMembersByUserId(userDetails.getUser().getUserId());
        return ResponseEntity.ok(members);
    }

}
