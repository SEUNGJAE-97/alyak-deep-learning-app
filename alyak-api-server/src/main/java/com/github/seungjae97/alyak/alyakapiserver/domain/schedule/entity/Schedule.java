package com.github.seungjae97.alyak.alyakapiserver.domain.schedule.entity;

import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.Pill;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "medication_schedules")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Schedule {

    @Id
    @Column(name = "schedule_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pill_id", nullable = false)
    private Pill pill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private ScheduleStatus status;

    @Column(name = "schedule_time", nullable = false)
    private LocalDateTime scheduleTime;

    @Column(name = "schedule_start_time", nullable = false)
    private LocalDateTime scheduleStartTime;

    @Column(name = "schedule_end_time", nullable = false)
    private LocalDateTime scheduleEndTime;

    @Column(name = "schedule_dosage", nullable = false)
    private Integer scheduleDosage;
}
