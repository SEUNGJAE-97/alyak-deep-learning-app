package com.github.seungjae97.alyak.alyakapiserver.domain.schedule.controller;

import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.UserDetailsImpl;
import com.github.seungjae97.alyak.alyakapiserver.domain.schedule.dto.request.ScheduleBackupRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.schedule.dto.response.ScheduleBackupResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.schedule.service.ScheduleBackupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
@Tag(name = "07. 스케쥴", description = "스케쥴 조회 및 백업·복구")
public class ScheduleController {

    private final ScheduleBackupService scheduleBackupService;

    @GetMapping("/searchFamily")
    @Operation(summary = "회원이 속한 가족들의 스케쥴 정보를 조회합니다.", description = "가족 구성원들의 스케줄 백업을 조회합니다.")
    public ResponseEntity<List<ScheduleBackupResponse>> getScheduleByFamilyId(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<ScheduleBackupResponse> responses = scheduleBackupService.findAllForFamily(userDetails.getUser().getUserId());
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/backup")
    @Operation(summary = "스케줄 백업 저장", description = "서버에 스케줄 규칙을 저장합니다. 재설치 복구용입니다.")
    public ResponseEntity<List<ScheduleBackupResponse>> backupSchedules(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody List<ScheduleBackupRequest> requests
    ) {
        List<ScheduleBackupResponse> saved = scheduleBackupService.saveBackups(userDetails.getUser().getUserId(), requests);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/restore")
    @Operation(summary = "스케줄 복구", description = "앱 재설치 시 서버에 저장된 스케줄 백업 목록을 조회합니다.")
    public ResponseEntity<List<ScheduleBackupResponse>> restoreSchedules(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<ScheduleBackupResponse> list = scheduleBackupService.findAllForUser(userDetails.getUser().getUserId());
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{scheduleId}")
    @Operation(summary = "스케줄 백업 삭제", description = "본인이 저장한 백업 스케줄 한 건을 삭제합니다.")
    public ResponseEntity<Void> deleteBackup(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long scheduleId
    ) {
        scheduleBackupService.deleteBackup(userDetails.getUser().getUserId(), scheduleId);
        return ResponseEntity.noContent().build();
    }
}
