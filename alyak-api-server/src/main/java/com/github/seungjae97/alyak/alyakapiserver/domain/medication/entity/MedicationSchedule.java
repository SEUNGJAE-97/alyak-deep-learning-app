package com.github.seungjae97.alyak.alyakapiserver.domain.medication.entity;

import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.Pill;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "medication_schedules")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MedicationSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pill_id", nullable = false)
    private Pill pill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    @Column(name = "schedule_time")
    private LocalDateTime scheduleTime;

    @Column(name = "schedule_start_time")
    private LocalDateTime scheduleStartTime;

    @Column(name = "schedule_end_time")
    private LocalDateTime scheduleEndTime;

    @Column(name = "schedule_dosage")
    private Integer scheduleDosage;

    @Builder
    public MedicationSchedule(User user, Pill pill, Status status,
                              LocalDateTime scheduleTime, LocalDateTime scheduleStartTime,
                              LocalDateTime scheduleEndTime, Integer scheduleDosage) {
        this.user = user;
        this.pill = pill;
        this.status = status;
        this.scheduleTime = scheduleTime;
        this.scheduleStartTime = scheduleStartTime;
        this.scheduleEndTime = scheduleEndTime;
        this.scheduleDosage = scheduleDosage;
    }

    /**
     * 복용 상태 및 실제 복용 시간을 업데이트하는 비즈니스 메서드
     * @param newStatus 새로운 상태 엔티티
     * @param actualTakenTime 실제 복용 시간
     */
    public void updateStatus(Status newStatus, LocalDateTime actualTakenTime) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.status = newStatus;
        this.scheduleStartTime = actualTakenTime;
    }

    /**
     * 복용 용량을 변경하는 비즈니스 메서드
     * @param newDosage 새로운 복용량
     */
    public void updateDosage(Integer newDosage) {
        if (newDosage == null || newDosage <= 0) {
            throw new IllegalArgumentException("Dosage must be positive");
        }

        this.scheduleDosage = newDosage;
    }
}
