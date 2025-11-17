package com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity;

import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "pill_appearance")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PillAppearance {

    @Id
    @Column(name = "pill_id")
    private Long pillId;

    @Column(name = "pill_front")
    private String pillFront;

    @Column(name = "pill_back")
    private String pillBack;

    @Column(name = "pill_classification")
    private String pillClassification;

    @Column(name = "pill_type")
    private String pillType;

    @Column(name = "shape_id")
    private Long shapeId;

    @Column(name = "color_class1_id")
    private Long colorClass1Id;

    @Column(name = "color_class2_id")
    private Long colorClass2Id;

    @Column(name = "pill_form")
    private String pillForm;

    @Column(name = "line_front")
    private String lineFront;

    @Column(name = "line_back")
    private String lineBack;

    @Column(name = "mark_code_front_anal")
    private String markCodeFrontAnal;

    @Column(name = "mark_code_back_anal")
    private String markCodeBackAnal;

    //TODO : 색상 코드 두개, 분할선 코드 두개, 식별코드 두개
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shape_id", insertable = false, updatable = false)
    private PillShape pillShapeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_class1_id", insertable = false, updatable = false)
    private PillColor pillColorClass1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_class2_id", insertable = false, updatable = false)
    private PillColor pillColorClass2;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId("pillId")
    @JoinColumn(name = "pill_id", nullable = false)
    private Pill pill;
}
