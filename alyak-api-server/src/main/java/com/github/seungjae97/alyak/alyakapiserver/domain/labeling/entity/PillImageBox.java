package com.github.seungjae97.alyak.alyakapiserver.domain.labeling.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "pill_image_box", uniqueConstraints = {
        @UniqueConstraint(name = "uq_pill_image_box_image_index", columnNames = {"image_id", "box_index"})
}, indexes = {
        @Index(name = "idx_pill_image_box_image_id", columnList = "image_id")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PillImageBox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id", nullable = false)
    private PillImageData imageData;

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
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public void assignImageData(PillImageData imageData) {
        this.imageData = imageData;
    }
}
