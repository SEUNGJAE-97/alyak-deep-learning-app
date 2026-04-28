package com.github.seungjae97.alyak.alyakapiserver.domain.admin.controller;

import com.github.seungjae97.alyak.alyakapiserver.domain.training.dto.response.ModelArchiveCompareResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.dto.response.ModelArchiveDetailResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.dto.response.ModelArchiveResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.entity.ModelArchiveStatus;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.service.ModelArchiveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@AdminApiController
@RequiredArgsConstructor
@RequestMapping("/api/admin/archives")
@Tag(name = "10.Admin-Archives", description = "관리자 모델 아카이브 API")
public class AdminArchiveController {

    private final ModelArchiveService modelArchiveService;

    @GetMapping("/models")
    @Operation(summary = "모델 아카이브 목록 조회", description = "모델 버전 목록을 상태/페이지네이션으로 조회합니다.")
    public ResponseEntity<Page<ModelArchiveResponse>> getModels(
            @RequestParam(required = false) ModelArchiveStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return ResponseEntity.ok(modelArchiveService.getArchives(status, pageable));
    }

    @GetMapping("/models/{id}")
    @Operation(summary = "모델 아카이브 상세 조회", description = "지정한 모델 아카이브 상세 정보를 조회합니다.")
    public ResponseEntity<ModelArchiveDetailResponse> getModel(@PathVariable Long id) {
        return ResponseEntity.ok(modelArchiveService.getArchive(id));
    }

    @GetMapping("/models/compare")
    @Operation(summary = "모델 성능 비교", description = "기준 모델과 대상 모델의 주요 지표를 비교 조회합니다.")
    public ResponseEntity<ModelArchiveCompareResponse> compare(
            @RequestParam Long baseId,
            @RequestParam Long targetId
    ) {
        return ResponseEntity.ok(modelArchiveService.compare(baseId, targetId));
    }
}
