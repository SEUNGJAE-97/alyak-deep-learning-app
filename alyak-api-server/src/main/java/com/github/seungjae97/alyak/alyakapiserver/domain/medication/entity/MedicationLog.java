package com.github.seungjae97.alyak.alyakapiserver.domain.medication.entity;

import com.github.seungjae97.alyak.alyakapiserver.domain.medication.enums.MedicationStatus;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 복용 기록 (보호자 조회·통계용).
 */
@Entity
@Getter
@Table(name = "medication_log", indexes = {
        @Index(name = "idx_medication_log_user_scheduled", columnList = "user_id,scheduled_time")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class MedicationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "pill_name", nullable = false)
    private String pillName;

    @Column(name = "dosage", nullable = false)
    private Integer dosage;

    @Column(name = "scheduled_time", nullable = false)
    private LocalDateTime scheduledTime;

    @Column(name = "taken_time")
    private LocalDateTime takenTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private MedicationStatus status;

    @Column(name = "logged_at")
    private LocalDateTime loggedAt;

    @PrePersist
    void prePersist() {
        if (loggedAt == null) {
            loggedAt = LocalDateTime.now();
        }
    }
}
