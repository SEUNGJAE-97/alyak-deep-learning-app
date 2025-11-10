package com.github.seungjae97.alyak.alyakapiserver.domain.medication.controller;

import com.github.seungjae97.alyak.alyakapiserver.domain.medication.entity.UserMedication;
import com.github.seungjae97.alyak.alyakapiserver.domain.medication.service.UserMedicationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user-medications")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "09. 유저 복용 알약", description = "유저가 복용중인 알약의 API")
public class UserMedicationController {
    
    private final UserMedicationService userMedicationService;
    
    @GetMapping
    public ResponseEntity<List<UserMedication>> getAllUserMedications() {
        List<UserMedication> userMedications = userMedicationService.getAll();
        return ResponseEntity.ok(userMedications);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserMedication> getUserMedicationById(@PathVariable Long id) {
        Optional<UserMedication> userMedication = userMedicationService.getById(id);
        return userMedication.map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserMedication>> getUserMedicationsByUserId(@PathVariable Long userId) {
        List<UserMedication> userMedications = userMedicationService.getByUserId(userId);
        return ResponseEntity.ok(userMedications);
    }
    
    @PostMapping
    public ResponseEntity<UserMedication> createUserMedication(@RequestBody UserMedication userMedication) {
        UserMedication createdUserMedication = userMedicationService.create(userMedication);
        return ResponseEntity.ok(createdUserMedication);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UserMedication> updateUserMedication(@PathVariable Long id, @RequestBody UserMedication userMedication) {
        Optional<UserMedication> existingUserMedication = userMedicationService.getById(id);
        if (existingUserMedication.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        UserMedication updatedUserMedication = userMedicationService.update(userMedication);
        return ResponseEntity.ok(updatedUserMedication);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserMedication(@PathVariable Long id) {
        Optional<UserMedication> userMedication = userMedicationService.getById(id);
        if (userMedication.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        userMedicationService.delete(id);
        return ResponseEntity.noContent().build();
    }
} 