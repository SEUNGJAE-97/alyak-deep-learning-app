package com.github.seungjae97.alyak.alyakapiserver.domain.admin.controller;

import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.dto.request.CreateLabelingItemRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.dto.request.BulkUpdateLabelingStatusRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.dto.request.UpdateLabelingBoxesRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.dto.response.CreateLabelingItemResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.dto.response.LabelingItemDetailResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.dto.response.LabelingItemResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.entity.DataStatus;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.service.LabelingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AdminApiController
@RequiredArgsConstructor
@RequestMapping("/api/admin/labeling")
@Tag(name = "08.Admin-Labeling", description = "관리자 라벨링 API")
public class AdminLabelingController {

    private final LabelingService labelingService;

    @PostMapping(value = "/items", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "라벨링 항목 생성", description = "원본 이미지 파일과 바운딩박스 좌표를 저장합니다.")
    public ResponseEntity<CreateLabelingItemResponse> createItem(
            @RequestPart("image") MultipartFile image,
            @RequestPart("request") CreateLabelingItemRequest request
    ) {
        return ResponseEntity.ok(labelingService.createItem(image, request));
    }

    @GetMapping("/items")
    @Operation(summary = "라벨링 항목 목록 조회", description = "status 필터와 페이지네이션으로 라벨링 항목을 조회합니다.")
    public ResponseEntity<Page<LabelingItemResponse>> getItems(
            @RequestParam(required = false) DataStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return ResponseEntity.ok(labelingService.getItems(status, pageable));
    }

    @GetMapping("/items/{id}")
    @Operation(summary = "라벨링 항목 상세 조회", description = "지정한 항목의 원본 이미지와 바운딩박스 좌표를 조회합니다.")
    public ResponseEntity<LabelingItemDetailResponse> getItemDetail(@PathVariable Long id) {
        return ResponseEntity.ok(labelingService.getItemDetail(id));
    }

    @PutMapping("/items/{id}/boxes")
    @Operation(summary = "라벨링 박스 수정", description = "지정한 항목의 바운딩박스 좌표를 전체 교체 저장합니다.")
    public ResponseEntity<LabelingItemDetailResponse> updateBoxes(
            @PathVariable Long id,
            @RequestBody UpdateLabelingBoxesRequest request
    ) {
        return ResponseEntity.ok(labelingService.updateBoxes(id, request));
    }

    @PostMapping("/items/{id}/approve")
    @Operation(summary = "라벨링 항목 승인", description = "지정한 항목 상태를 TRAINING_SET으로 변경합니다.")
    public ResponseEntity<LabelingItemResponse> approve(@PathVariable Long id) {
        return ResponseEntity.ok(labelingService.approve(id));
    }

    @PostMapping("/items/{id}/reject")
    @Operation(summary = "라벨링 항목 반려", description = "지정한 항목 상태를 TRASH로 변경합니다.")
    public ResponseEntity<LabelingItemResponse> reject(@PathVariable Long id) {
        return ResponseEntity.ok(labelingService.reject(id));
    }

    @PatchMapping("/items/bulk/status")
    @Operation(summary = "라벨링 항목 상태 일괄 변경", description = "선택한 항목들의 상태를 INBOX/TRAINING_SET/TRASH로 일괄 변경합니다.")
    public ResponseEntity<List<LabelingItemResponse>> bulkUpdateStatus(
            @RequestBody BulkUpdateLabelingStatusRequest request
    ) {
        return ResponseEntity.ok(labelingService.updateStatuses(request.getIds(), request.getStatus()));
    }
}
