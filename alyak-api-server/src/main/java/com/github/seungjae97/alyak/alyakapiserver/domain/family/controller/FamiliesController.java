package com.github.seungjae97.alyak.alyakapiserver.domain.family.controller;

import com.github.seungjae97.alyak.alyakapiserver.domain.family.entity.Families;
import com.github.seungjae97.alyak.alyakapiserver.domain.family.service.FamiliesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/families")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Families", description = "가족 관리 API")
public class FamiliesController {
    
    private final FamiliesService familiesService;

    @GetMapping
    @Operation(summary = "모든 가족 조회", description = "등록된 모든 가족 정보를 조회합니다.")
    public ResponseEntity<List<Families>> getAllFamilies() {
        List<Families> families = familiesService.getAll();
        return ResponseEntity.ok(families);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "가족 조회", description = "ID로 특정 가족 정보를 조회합니다.")
    public ResponseEntity<Families> getFamilyById(@PathVariable Long id) {
        Optional<Families> family = familiesService.getById(id);
        return family.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @Operation(summary = "가족 생성", description = "새로운 가족을 생성합니다.")
    public ResponseEntity<Families> createFamily(@RequestBody Families family) {
        Families createdFamily = familiesService.createFamily(family);
        return ResponseEntity.ok(createdFamily);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "가족 정보 수정", description = "기존 가족 정보를 수정합니다.")
    public ResponseEntity<Families> updateFamily(@PathVariable Long id, @RequestBody Families family) {
        Optional<Families> existingFamily = familiesService.getById(id);
        if (existingFamily.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        family.setId(id);
        Families updatedFamily = familiesService.updateFamily(family);
        return ResponseEntity.ok(updatedFamily);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "가족 삭제", description = "가족을 삭제합니다.")
    public ResponseEntity<Void> deleteFamily(@PathVariable Long id) {
        Optional<Families> family = familiesService.getById(id);
        if (family.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        familiesService.deleteFamily(id);
        return ResponseEntity.noContent().build();
    }
} 