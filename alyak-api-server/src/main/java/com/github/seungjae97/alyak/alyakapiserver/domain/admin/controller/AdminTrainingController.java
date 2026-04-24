package com.github.seungjae97.alyak.alyakapiserver.domain.admin.controller;

import com.github.seungjae97.alyak.alyakapiserver.domain.training.dto.request.CreateTrainingJobRequest;
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
}
