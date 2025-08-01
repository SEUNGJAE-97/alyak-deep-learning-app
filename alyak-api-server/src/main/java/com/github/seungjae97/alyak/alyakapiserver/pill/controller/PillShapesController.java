package com.github.seungjae97.alyak.alyakapiserver.pill.controller;

import com.github.seungjae97.alyak.alyakapiserver.pill.entity.PillShapes;
import com.github.seungjae97.alyak.alyakapiserver.pill.service.PillShapesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pill-shapes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PillShapesController {
    
    private final PillShapesService pillShapesService;
    
    @GetMapping
    public ResponseEntity<List<PillShapes>> getAllPillShapes() {
        List<PillShapes> pillShapes = pillShapesService.getAll();
        return ResponseEntity.ok(pillShapes);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PillShapes> getPillShapeById(@PathVariable Long id) {
        Optional<PillShapes> pillShape = pillShapesService.getById(id);
        return pillShape.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<PillShapes> createPillShape(@RequestBody PillShapes pillShape) {
        PillShapes createdPillShape = pillShapesService.create(pillShape);
        return ResponseEntity.ok(createdPillShape);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PillShapes> updatePillShape(@PathVariable Long id, @RequestBody PillShapes pillShape) {
        Optional<PillShapes> existingPillShape = pillShapesService.getById(id);
        if (existingPillShape.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        pillShape.setId(id);
        PillShapes updatedPillShape = pillShapesService.update(pillShape);
        return ResponseEntity.ok(updatedPillShape);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePillShape(@PathVariable Long id) {
        Optional<PillShapes> pillShape = pillShapesService.getById(id);
        if (pillShape.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        pillShapesService.delete(id);
        return ResponseEntity.noContent().build();
    }
} 