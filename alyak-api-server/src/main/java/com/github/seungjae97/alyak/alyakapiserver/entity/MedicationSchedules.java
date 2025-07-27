package com.github.seungjae97.alyak.alyakapiserver.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "medication_schedules")
@Getter
@Setter
@NoArgsConstructor
public class MedicationSchedules {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_medication_id", nullable = false)
    private UserMedication userMedication;
    
    @Column(name = "scheduled_time", nullable = false)
    private LocalDateTime scheduledTime;
    
    @Column(name = "taken_time")
    private LocalDateTime takenTime;
    
    @Enumerated(EnumType.STRING)
    private Status status;
    
    public enum Status {
        PENDING, TAKEN, MISSED
    }
} 