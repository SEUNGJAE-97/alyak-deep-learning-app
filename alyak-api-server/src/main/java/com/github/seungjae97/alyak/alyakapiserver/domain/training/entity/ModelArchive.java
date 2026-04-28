package com.github.seungjae97.alyak.alyakapiserver.domain.training.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "model_archive", indexes = {
        @Index(name = "idx_model_archive_created_at", columnList = "created_at"),
        @Index(name = "idx_model_archive_status", columnList = "status")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ModelArchive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "version", nullable = false, unique = true, length = 128)
    private String version;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    @Builder.Default
    private ModelArchiveStatus status = ModelArchiveStatus.ARCHIVED;

    @Column(name = "run_dir", nullable = false, unique = true, length = 500)
    private String runDir;

    @Column(name = "model_path", length = 500)
    private String modelPath;

    @Column(name = "args_path", nullable = false, length = 500)
    private String argsPath;

    @Column(name = "results_path", nullable = false, length = 500)
    private String resultsPath;

    @Column(name = "dataset_name", length = 255)
    private String datasetName;

    @Column(name = "image_count")
    private Integer imageCount;

    @Column(name = "augmentation_summary", length = 255)
    private String augmentationSummary;

    @Column(name = "epochs")
    private Integer epochs;

    @Column(name = "batch_size")
    private Integer batchSize;

    @Column(name = "learning_rate", precision = 14, scale = 8)
    private BigDecimal learningRate;

    @Column(name = "optimizer", length = 64)
    private String optimizer;

    @Column(name = "freeze_layers", length = 64)
    private String freezeLayers;

    @Column(name = "best_map50", precision = 12, scale = 6)
    private BigDecimal bestMap50;

    @Column(name = "best_map50_95", precision = 12, scale = 6)
    private BigDecimal bestMap50_95;

    @Column(name = "best_precision", precision = 12, scale = 6)
    private BigDecimal bestPrecision;

    @Column(name = "best_recall", precision = 12, scale = 6)
    private BigDecimal bestRecall;

    @Column(name = "best_fitness", precision = 12, scale = 6)
    private BigDecimal bestFitness;

    @Column(name = "best_loss", precision = 12, scale = 6)
    private BigDecimal bestLoss;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
