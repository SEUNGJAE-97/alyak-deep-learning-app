package com.github.seungjae97.alyak.alyakapiserver.domain.admin.controller;

import com.github.seungjae97.alyak.alyakapiserver.domain.training.dto.request.CreateTrainingJobRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.client.FastApiTrainingClient;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.client.dto.FastApiSystemStatusResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.dto.response.TrainingJobResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.service.TrainingJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AdminApiController
@RequiredArgsConstructor
@RequestMapping("/api/admin/training")
@Tag(name = "09.Admin-Training", description = "관리자 파인튜닝 작업 API")
public class AdminTrainingController {

    private final TrainingJobService trainingJobService;
    private final FastApiTrainingClient fastApiTrainingClient;

    @PostMapping("/jobs")
    @Operation(summary = "학습 작업 생성", description = "FastAPI 학습 작업을 생성하고 Spring DB에 이력을 저장합니다.")
    public ResponseEntity<TrainingJobResponse> createJob(@RequestBody CreateTrainingJobRequest request) {
        return ResponseEntity.ok(trainingJobService.createJob(request));
    }

    @GetMapping("/jobs/{id}")
    @Operation(summary = "학습 작업 상세 조회", description = "학습 작업의 상태와 진행률을 조회합니다.")
    public ResponseEntity<TrainingJobResponse> getJob(@PathVariable Long id) {
        return ResponseEntity.ok(trainingJobService.getJob(id));
    }

    @GetMapping("/jobs")
    @Operation(summary = "학습 작업 목록 조회", description = "학습 작업 목록을 페이지네이션으로 조회합니다.")
    public ResponseEntity<Page<TrainingJobResponse>> getJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return ResponseEntity.ok(trainingJobService.getJobs(pageable));
    }

    @GetMapping("/system-status")
    @Operation(summary = "학습 서버 상태 조회", description = "Spring 프록시를 통해 FastAPI 시스템 상태를 조회합니다.")
    public ResponseEntity<FastApiSystemStatusResponse> getSystemStatus() {
        try {
            FastApiSystemStatusResponse status = fastApiTrainingClient.getSystemStatus();
            if (status == null) {
                return ResponseEntity.ok(buildOfflineStatus("학습 서버 상태 응답이 비어 있습니다."));
            }
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.ok(buildOfflineStatus("재연결 시도 중..."));
        }
    }

    private FastApiSystemStatusResponse buildOfflineStatus(String message) {
        FastApiSystemStatusResponse status = new FastApiSystemStatusResponse();
        status.setStatus("OFFLINE");
        status.setConnected(false);
        status.setMessage(message);
        status.setDevice("cpu");
        status.setCpuName("알 수 없음");
        return status;
    }
}
