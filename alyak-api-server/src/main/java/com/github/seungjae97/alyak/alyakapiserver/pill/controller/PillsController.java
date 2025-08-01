package com.github.seungjae97.alyak.alyakapiserver.pill.controller;

import com.github.seungjae97.alyak.alyakapiserver.pill.entity.Pills;
import com.github.seungjae97.alyak.alyakapiserver.pill.service.PillsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pills")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor

public class PillsController {
    
    private final PillsService pillsService;
    
    @GetMapping
    public ResponseEntity<List<Pills>> getAllPills() {
        List<Pills> pills = pillsService.getAll();
        return ResponseEntity.ok(pills);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Pills> getPillById(@PathVariable Long id) {
        Optional<Pills> pill = pillsService.getById(id);
        return pill.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/shape/{pillShapeId}")
    public ResponseEntity<List<Pills>> getPillsByShapeId(@PathVariable Long pillShapeId) {
        List<Pills> pills = pillsService.getByPillShapeId(pillShapeId);
        return ResponseEntity.ok(pills);
    }
    
    @PostMapping
    public ResponseEntity<Pills> createPill(@RequestBody Pills pill) {
        Pills createdPill = pillsService.create(pill);
        return ResponseEntity.ok(createdPill);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Pills> updatePill(@PathVariable Long id, @RequestBody Pills pill) {
        Optional<Pills> existingPill = pillsService.getById(id);
        if (existingPill.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        pill.setId(id);
        Pills updatedPill = pillsService.update(pill);
        return ResponseEntity.ok(updatedPill);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePill(@PathVariable Long id) {
        Optional<Pills> pill = pillsService.getById(id);
        if (pill.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        pillsService.delete(id);
        return ResponseEntity.noContent().build();
    }
} 