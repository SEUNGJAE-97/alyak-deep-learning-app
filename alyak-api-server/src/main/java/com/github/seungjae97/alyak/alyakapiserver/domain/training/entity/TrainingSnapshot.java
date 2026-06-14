package com.github.seungjae97.alyak.alyakapiserver.domain.training.entity;

import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.entity.PillImageBox;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.entity.PillImageData;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "training_snapshot", indexes = {
        @Index(name = "idx_training_snapshot_job_id", columnList = "training_job_id")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class TrainingSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_job_id", nullable = false)
    private TrainingJob trainingJob;

    @Column(name = "image_path", nullable = false, length = 500)
    private String imagePath;

    @Column(name = "box_index", nullable = false)
    private Integer boxIndex;

    @Column(name = "x_min", nullable = false, precision = 7, scale = 6)
    private BigDecimal xMin;

    @Column(name = "y_min", nullable = false, precision = 7, scale = 6)
    private BigDecimal yMin;

    @Column(name = "x_max", nullable = false, precision = 7, scale = 6)
    private BigDecimal xMax;

    @Column(name = "y_max", nullable = false, precision = 7, scale = 6)
    private BigDecimal yMax;


    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public static TrainingSnapshot of(TrainingJob job, PillImageData image, PillImageBox box) {
        return TrainingSnapshot.builder()
                .trainingJob(job)
                .imagePath(image.getImagePath())
                .boxIndex(box.getBoxIndex())
                .xMin(box.getXMin())
                .yMin(box.getYMin())
                .xMax(box.getXMax())
                .yMax(box.getYMax())
                .build();
    }
}
