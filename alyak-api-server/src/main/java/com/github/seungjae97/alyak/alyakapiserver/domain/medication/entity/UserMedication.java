package com.github.seungjae97.alyak.alyakapiserver.domain.medication.entity;

import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.Pill;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_medications")
@Getter
@Setter
@NoArgsConstructor
public class UserMedication {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pill_id", nullable = false)
    private Pill pill;
    
    @Column(nullable = false)
    private Integer dosage;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 양방향 관계 설정
    @OneToMany(mappedBy = "userMedication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MedicationSchedule> medicationSchedules = new ArrayList<>();
} 