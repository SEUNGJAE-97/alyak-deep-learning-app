package com.github.seungjae97.alyak.alyakapiserver.domain.medication.controller;

import com.github.seungjae97.alyak.alyakapiserver.domain.medication.entity.MedicationSchedules;
import com.github.seungjae97.alyak.alyakapiserver.domain.medication.service.MedicationSchedulesService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/medication-schedules")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "08. 약 복용 스케쥴", description = "알약 복용 스케쥴 관리 관련 API")
public class MedicationSchedulesController {
    
    private final MedicationSchedulesService medicationSchedulesService;
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MedicationSchedules>> getMedicationSchedulesByUserId(@PathVariable Long userId) {
        List<MedicationSchedules> medicationSchedules = medicationSchedulesService.getByUserId(userId);
        return ResponseEntity.ok(medicationSchedules);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MedicationSchedules> getMedicationScheduleById(@PathVariable Long id) {
        Optional<MedicationSchedules> medicationSchedule = medicationSchedulesService.getById(id);
        return medicationSchedule.map(ResponseEntity::ok)
                               .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/user-medication/{userMedicationId}")
    public ResponseEntity<List<MedicationSchedules>> getMedicationSchedulesByUserMedicationId(@PathVariable Long userMedicationId) {
        List<MedicationSchedules> medicationSchedules = medicationSchedulesService.getByUserMedicationId(userMedicationId);
        return ResponseEntity.ok(medicationSchedules);
    }
    
    @GetMapping("/schedule")
    public ResponseEntity<List<MedicationSchedules>> getMedicationSchedulesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<MedicationSchedules> medicationSchedules = medicationSchedulesService.getByScheduledTimeBetween(start, end);
        return ResponseEntity.ok(medicationSchedules);
    }
    
    @PostMapping
    public ResponseEntity<MedicationSchedules> createMedicationSchedule(@RequestBody MedicationSchedules medicationSchedule) {
        MedicationSchedules createdMedicationSchedule = medicationSchedulesService.create(medicationSchedule);
        return ResponseEntity.ok(createdMedicationSchedule);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<MedicationSchedules> updateMedicationSchedule(@PathVariable Long id, @RequestBody MedicationSchedules medicationSchedule) {
        Optional<MedicationSchedules> existingMedicationSchedule = medicationSchedulesService.getById(id);
        if (existingMedicationSchedule.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        medicationSchedule.setId(id);
        MedicationSchedules updatedMedicationSchedule = medicationSchedulesService.update(medicationSchedule);
        return ResponseEntity.ok(updatedMedicationSchedule);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedicationSchedule(@PathVariable Long id) {
        Optional<MedicationSchedules> medicationSchedule = medicationSchedulesService.getById(id);
        if (medicationSchedule.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        medicationSchedulesService.delete(id);
        return ResponseEntity.noContent().build();
    }
} 