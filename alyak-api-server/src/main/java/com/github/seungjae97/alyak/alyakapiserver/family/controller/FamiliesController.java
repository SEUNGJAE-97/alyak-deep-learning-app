package com.github.seungjae97.alyak.alyakapiserver.family.controller;

import com.github.seungjae97.alyak.alyakapiserver.family.entity.Families;
import com.github.seungjae97.alyak.alyakapiserver.family.service.FamiliesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/families")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FamiliesController {
    
    private final FamiliesService familiesService;

    @GetMapping
    public ResponseEntity<List<Families>> getAllFamilies() {
        List<Families> families = familiesService.getAll();
        return ResponseEntity.ok(families);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Families> getFamilyById(@PathVariable Long id) {
        Optional<Families> family = familiesService.getById(id);
        return family.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Families> createFamily(@RequestBody Families family) {
        Families createdFamily = familiesService.createFamily(family);
        return ResponseEntity.ok(createdFamily);
    }
    
    @PutMapping("/{id}")
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
    public ResponseEntity<Void> deleteFamily(@PathVariable Long id) {
        Optional<Families> family = familiesService.getById(id);
        if (family.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        familiesService.deleteFamily(id);
        return ResponseEntity.noContent().build();
    }
} 