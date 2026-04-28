package com.github.seungjae97.alyak.alyakapiserver.domain.training.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "training_job", indexes = {
        @Index(name = "idx_training_job_status", columnList = "status"),
        @Index(name = "idx_training_job_external_job_id", columnList = "external_job_id")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class TrainingJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    @Builder.Default
    private TrainingJobStatus status = TrainingJobStatus.PENDING;

    @Column(name = "dataset_filter", nullable = false, length = 64)
    private String datasetFilter;

    @Lob
    @Column(name = "params_json", nullable = false, columnDefinition = "TEXT")
    private String paramsJson;

    @Column(name = "external_job_id", length = 128)
    private String externalJobId;

    @Column(name = "progress", nullable = false)
    @Builder.Default
    private Integer progress = 0;

    @Column(name = "message", length = 500)
    private String message;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void markRunning(String externalJobId, String message) {
        this.status = TrainingJobStatus.RUNNING;
        this.externalJobId = externalJobId;
        this.progress = Math.max(this.progress, 0);
        this.message = message;
        this.startedAt = LocalDateTime.now();
    }

    public void markFailed(String message) {
        this.status = TrainingJobStatus.FAILED;
        this.message = message;
        this.finishedAt = LocalDateTime.now();
    }

    public void syncStatus(TrainingJobStatus status, Integer progress, String message) {
        this.status = status;
        if (progress != null) {
            this.progress = progress;
        }
        this.message = message;
        if (status == TrainingJobStatus.SUCCEEDED
                || status == TrainingJobStatus.FAILED
                || status == TrainingJobStatus.CANCELLED) {
            this.finishedAt = LocalDateTime.now();
        }
    }

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
