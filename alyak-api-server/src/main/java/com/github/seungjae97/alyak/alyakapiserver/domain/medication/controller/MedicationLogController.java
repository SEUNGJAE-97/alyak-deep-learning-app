package com.github.seungjae97.alyak.alyakapiserver.domain.medication.controller;

import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.UserDetailsImpl;
import com.github.seungjae97.alyak.alyakapiserver.domain.family.dto.response.DailyMedicationStat;
import com.github.seungjae97.alyak.alyakapiserver.domain.family.dto.response.MemberStats;
import com.github.seungjae97.alyak.alyakapiserver.domain.medication.dto.request.MedicationLogRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.medication.service.MedicationLogService;
import com.github.seungjae97.alyak.alyakapiserver.domain.schedule.service.MedicationStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/medication")
@RequiredArgsConstructor
@Tag(name = "08. 복용 기록", description = "복용 기록 저장 및 통계")
public class MedicationLogController {

    private final MedicationLogService medicationLogService;
    private final MedicationStatsService medicationStatsService;

    @PostMapping("/log")
    @Operation(
            summary = "복용 기록 저장",
            description = "복용 시 takenTime을 채워 전송합니다. 미복용(SKIPPED)은 알람 경과 후 클라이언트(예: WorkManager)에서 takenTime 없이 호출합니다."
    )
    public ResponseEntity<Void> log(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody MedicationLogRequest request
    ) {
        medicationLogService.log(userDetails.getUser().getUserId(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats")
    @Operation(summary = "본인 복용 통계 조회")
    public ResponseEntity<MemberStats> getStats(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(
                medicationStatsService.calculateMemberStats(userDetails.getUser().getUserId())
        );
    }

    @GetMapping("/stats/{userId}")
    @Operation(summary = "가족 복용 통계 조회", description = "같은 가족 구성원의 통계만 조회할 수 있습니다.")
    public ResponseEntity<MemberStats> getFamilyStats(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long userId
    ) {
        medicationLogService.assertCanViewMedicationStats(userDetails.getUser().getUserId(), userId);
        return ResponseEntity.ok(medicationStatsService.calculateMemberStats(userId));
    }

    @GetMapping("/weekly/{userId}")
    @Operation(summary = "최근 7일 통계 조회", description = "같은 가족 구성원의 일별 통계만 조회할 수 있습니다.")
    public ResponseEntity<List<DailyMedicationStat>> getWeeklyStats(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long userId
    ) {
        medicationLogService.assertCanViewMedicationStats(userDetails.getUser().getUserId(), userId);
        return ResponseEntity.ok(medicationStatsService.calculateWeeklyStats(userId));
    }
}
