package com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity;

import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "pill_appearance")
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

    @Column(name = "color_id")
    private Long colorId;

    @Column(name = "pill_score")
    private String pillScore;

    @Column(name = "pill_ingredient")
    private String pillIngredient;

    @Column(name = "pill_form")
    private String pillForm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shape_id", insertable = false, updatable = false)
    private PillShape pillShapeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id", insertable = false, updatable = false)
    private PillColor pillColorId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId("pillId")
    @JoinColumn(name = "pill_id", nullable = false)
    private Pill pill;
}
