package com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pill_color")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PillColor {
    @Id
    @Column(name = "color_id")
    private Integer id;

    @Column(name = "color_name", nullable = false)
    private String name;
}
