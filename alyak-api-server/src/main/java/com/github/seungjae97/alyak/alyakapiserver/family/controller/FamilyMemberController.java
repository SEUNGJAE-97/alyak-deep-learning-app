package com.github.seungjae97.alyak.alyakapiserver.family.controller;

import com.github.seungjae97.alyak.alyakapiserver.family.entity.FamilyMember;
import com.github.seungjae97.alyak.alyakapiserver.family.service.FamilyMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/family-members")
@CrossOrigin(origins = "*")
public class FamilyMemberController {
    
    private final FamilyMemberService familyMemberService;
    
    public FamilyMemberController(FamilyMemberService familyMemberService) {
        this.familyMemberService = familyMemberService;
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<FamilyMember> getFamilyMemberById(@PathVariable Long id) {
        Optional<FamilyMember> familyMember = familyMemberService.getById(id);
        return familyMember.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/family/{familyId}")
    public ResponseEntity<List<FamilyMember>> getFamilyMembersByFamilyId(@PathVariable Long familyId) {
        List<FamilyMember> familyMembers = familyMemberService.getByFamilyId(familyId);
        return ResponseEntity.ok(familyMembers);
    }
    
    @PostMapping
    public ResponseEntity<FamilyMember> createFamilyMember(@RequestBody FamilyMember familyMember) {
        FamilyMember createdFamilyMember = familyMemberService.create(familyMember);
        return ResponseEntity.ok(createdFamilyMember);
    }
    
    @PutMapping("/{id}")
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
    public ResponseEntity<Void> deleteFamilyMember(@PathVariable Long id) {
        Optional<FamilyMember> familyMember = familyMemberService.getById(id);
        if (familyMember.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        familyMemberService.delete(id);
        return ResponseEntity.noContent().build();
    }
} 