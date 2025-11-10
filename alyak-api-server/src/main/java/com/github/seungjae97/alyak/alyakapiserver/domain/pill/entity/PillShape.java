package com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pill_shape")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PillShape {

    @Id
    @Column(name = "shape_id")
    private Integer id;

    @Column(name = "shape_name", nullable = false)
    private String name;
}
