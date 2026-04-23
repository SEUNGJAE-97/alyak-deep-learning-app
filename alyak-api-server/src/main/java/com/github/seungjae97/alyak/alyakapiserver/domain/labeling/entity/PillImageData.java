package com.github.seungjae97.alyak.alyakapiserver.domain.labeling.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "pill_image_data", indexes = {
        @Index(name = "idx_pill_image_data_status", columnList = "status")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PillImageData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_path", nullable = false, length = 500)
    private String imagePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    @Builder.Default
    private DataStatus status = DataStatus.INBOX;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "imageData", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PillImageBox> boxes = new ArrayList<>();

    public void addBox(PillImageBox box) {
        boxes.add(box);
        box.assignImageData(this);
    }

    public void replaceBoxes(List<PillImageBox> newBoxes) {
        boxes.clear();
        for (PillImageBox box : newBoxes) {
            addBox(box);
        }
    }

    public void updateStatus(DataStatus status) {
        this.status = status;
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
