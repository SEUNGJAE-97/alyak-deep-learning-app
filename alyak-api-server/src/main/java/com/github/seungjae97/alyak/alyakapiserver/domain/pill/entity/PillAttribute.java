package com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fill_attribute")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PillAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pill_id", nullable = false)
    private Pill pill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shape_id", nullable = false)
    private PillShape pillShape;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id", nullable = false)
    private PillColor pillColor;

    @Column(name = "detail")
    private String detail;

    @Column(name = "front")
    private String front;

    @Column(name = "back")
    private String back;

    @Column(name = "classification")
    private String classification;

    @Column(name = "pill_type")
    private Boolean pillType;
}
