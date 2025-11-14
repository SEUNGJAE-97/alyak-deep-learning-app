package com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "pill_shape")
public class PillShape {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "shape_id")
    private Long id;

    @Column(name = "shape_name")
    private String shapeName;
}
