package com.github.seungjae97.alyak.alyakapiserver.family.controller;

import com.github.seungjae97.alyak.alyakapiserver.family.entity.FamilyMember;
import com.github.seungjae97.alyak.alyakapiserver.family.service.FamilyMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/family-members")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Family Members", description = "가족 구성원 관리 API")
public class FamilyMemberController {
    
    private final FamilyMemberService familyMemberService;
    
    @GetMapping("/{id}")
    @Operation(summary = "가족 구성원 조회", description = "ID로 특정 가족 구성원을 조회합니다.")
    public ResponseEntity<FamilyMember> getFamilyMemberById(@PathVariable Long id) {
        Optional<FamilyMember> familyMember = familyMemberService.getById(id);
        return familyMember.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/family/{familyId}")
    @Operation(summary = "가족별 구성원 조회", description = "특정 가족의 모든 구성원을 조회합니다.")
    public ResponseEntity<List<FamilyMember>> getFamilyMembersByFamilyId(@PathVariable Long familyId) {
        List<FamilyMember> familyMembers = familyMemberService.getByFamilyId(familyId);
        return ResponseEntity.ok(familyMembers);
    }
    
    @PostMapping
    @Operation(summary = "가족 구성원 추가", description = "새로운 가족 구성원을 추가합니다.")
    public ResponseEntity<FamilyMember> createFamilyMember(@RequestBody FamilyMember familyMember) {
        FamilyMember createdFamilyMember = familyMemberService.create(familyMember);
        return ResponseEntity.ok(createdFamilyMember);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "가족 구성원 정보 수정", description = "가족 구성원 정보를 수정합니다.")
    public ResponseEntity<FamilyMember> updateFamilyMember(@PathVariable Long id, @RequestBody FamilyMember familyMember) {
        Optional<FamilyMember> existingFamilyMember = familyMemberService.getById(id);
        if (existingFamilyMember.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        familyMember.setId(id);
        FamilyMember updatedFamilyMember = familyMemberService.update(familyMember);
        return ResponseEntity.ok(updatedFamilyMember);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "가족 구성원 삭제", description = "가족 구성원을 삭제합니다.")
    public ResponseEntity<Void> deleteFamilyMember(@PathVariable Long id) {
        Optional<FamilyMember> familyMember = familyMemberService.getById(id);
        if (familyMember.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        familyMemberService.delete(id);
        return ResponseEntity.noContent().build();
    }
} 