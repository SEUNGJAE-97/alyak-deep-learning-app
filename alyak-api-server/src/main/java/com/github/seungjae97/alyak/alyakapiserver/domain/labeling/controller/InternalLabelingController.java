package com.github.seungjae97.alyak.alyakapiserver.domain.labeling.controller;

import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.dto.response.LabelingItemDetailResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.dto.response.LabelingItemResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.entity.DataStatus;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.service.LabelingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/internal/labeling")
public class InternalLabelingController {

    private final LabelingService labelingService;

    @Value("${training.callback.token:local-training-callback-token}")
    private String internalToken;

    @GetMapping("/items")
    public ResponseEntity<Page<LabelingItemResponse>> getItems(
            @RequestHeader(value = "X-Internal-Token", required = false) String requestToken,
            @RequestParam(defaultValue = "TRAINING_SET") DataStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int pageSize
    ) {
        if (!isAuthorized(requestToken)) {
            return ResponseEntity.status(403).build();
        }
        Pageable pageable = PageRequest.of(page, pageSize);
        return ResponseEntity.ok(labelingService.getItems(status, pageable));
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<LabelingItemDetailResponse> getItemDetail(
            @RequestHeader(value = "X-Internal-Token", required = false) String requestToken,
            @PathVariable Long id
    ) {
        if (!isAuthorized(requestToken)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(labelingService.getItemDetail(id));
    }

    private boolean isAuthorized(String requestToken) {
        return requestToken != null && requestToken.equals(internalToken);
    }
}
