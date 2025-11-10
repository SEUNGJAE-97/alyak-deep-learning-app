package com.github.seungjae97.alyak.alyakapiserver.domain.pill.controller;

import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.Pill;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.service.PillsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pills")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "10. 알약 관리", description = "알약 관련 API")
public class PillsController {
    
    private final PillsService pillsService;
    
    @GetMapping
    public ResponseEntity<List<Pill>> getAllPills() {
        List<Pill> pills = pillsService.getAll();
        return ResponseEntity.ok(pills);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Pill> getPillById(@PathVariable Long id) {
        Optional<Pill> pill = pillsService.getById(id);
        return pill.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/shape/{pillShapeId}")
    public ResponseEntity<List<Pill>> getPillsByShapeId(@PathVariable Long pillShapeId) {
        List<Pill> pills = pillsService.getByPillShapeId(pillShapeId);
        return ResponseEntity.ok(pills);
    }
    
    @PostMapping
    public ResponseEntity<Pill> createPill(@RequestBody Pill pill) {
        Pill createdPill = pillsService.create(pill);
        return ResponseEntity.ok(createdPill);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Pill> updatePill(@PathVariable Long id, @RequestBody Pill pill) {
        Optional<Pill> existingPill = pillsService.getById(id);
        if (existingPill.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Pill updatedPill = pillsService.update(pill);
        return ResponseEntity.ok(updatedPill);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePill(@PathVariable Long id) {
        Optional<Pill> pill = pillsService.getById(id);
        if (pill.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        pillsService.delete(id);
        return ResponseEntity.noContent().build();
    }
} 