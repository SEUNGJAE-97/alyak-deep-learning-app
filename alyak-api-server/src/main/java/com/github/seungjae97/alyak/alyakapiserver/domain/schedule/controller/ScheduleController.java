package com.github.seungjae97.alyak.alyakapiserver.domain.schedule.controller;

import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.UserDetailsImpl;
import com.github.seungjae97.alyak.alyakapiserver.domain.schedule.dto.response.UserScheduleResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.schedule.service.ScheduleService;
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
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
@Tag(name = "07. 스케쥴", description = "스케쥴 정보를 조회합니다.")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping("/search")
    @Operation(summary = "회원의 스케쥴 정보를 조회합니다.", description = "사용자한테 등록된 스케쥴을 조회하는 API")
    public ResponseEntity<List<UserScheduleResponse>> getScheduleByUserId(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<UserScheduleResponse> responses = scheduleService.findAllByUserId(userDetails.getUser().getUserId());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/searchFamily")
    @Operation(summary = "회원이 속한 가족들의 스케쥴 정보를 조회합니다.", description = "가족 구성원으로 등록된 사람들의 스케쥴을 조회")
    public ResponseEntity<List<UserScheduleResponse>> getScheduleByFamilyId(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<UserScheduleResponse> responses = scheduleService.findAllByUserIdFromFamily(userDetails.getUser().getFamily().getId());
        return ResponseEntity.ok(responses);
    }
}
