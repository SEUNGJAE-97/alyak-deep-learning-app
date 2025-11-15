package com.github.seungjae97.alyak.alyakapiserver.domain.pill.controller;

import com.github.seungjae97.alyak.alyakapiserver.domain.pill.dto.request.PillSearchRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.Pill;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.service.PillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pill")
@RequiredArgsConstructor
@Tag(name = "03.알약", description = "알약 정보 관련 API")
public class PillController {

    private final PillService pillService;

    @GetMapping("/find")
    @Operation(summary = "검색", description = "알약명을 기준으로 세부정보를 조회한다.")
    public ResponseEntity<?> findPill(@RequestParam String pillName) {
        pillService.findPill(pillName);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    @Operation(summary = "검색", description = "색상, 외형, 제형, 분할선을 기준으로 조회한다.")
    public ResponseEntity<?> searchPill(
            @ParameterObject PillSearchRequest req
    ) {
        return ResponseEntity.ok(pillService.searchPill(req));
    }
}
