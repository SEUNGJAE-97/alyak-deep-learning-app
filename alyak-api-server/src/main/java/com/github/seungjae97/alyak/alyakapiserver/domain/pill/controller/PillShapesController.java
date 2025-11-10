package com.github.seungjae97.alyak.alyakapiserver.domain.pill.controller;

import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.PillShape;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.service.PillShapesService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pill-shapes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "11. 알약 외형", description = "알약 외형 관련 API")
public class PillShapesController {
    
    private final PillShapesService pillShapesService;
    
    @GetMapping
    public ResponseEntity<List<PillShape>> getAllPillShapes() {
        List<PillShape> pillShapes = pillShapesService.getAll();
        return ResponseEntity.ok(pillShapes);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PillShape> getPillShapeById(@PathVariable Long id) {
        Optional<PillShape> pillShape = pillShapesService.getById(id);
        return pillShape.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<PillShape> createPillShape(@RequestBody PillShape pillShape) {
        PillShape createdPillShape = pillShapesService.create(pillShape);
        return ResponseEntity.ok(createdPillShape);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PillShape> updatePillShape(@PathVariable Long id, @RequestBody PillShape pillShape) {
        Optional<PillShape> existingPillShape = pillShapesService.getById(id);
        if (existingPillShape.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        PillShape updatedPillShape = pillShapesService.update(pillShape);
        return ResponseEntity.ok(updatedPillShape);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePillShape(@PathVariable Long id) {
        Optional<PillShape> pillShape = pillShapesService.getById(id);
        if (pillShape.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        pillShapesService.delete(id);
        return ResponseEntity.noContent().build();
    }
} 