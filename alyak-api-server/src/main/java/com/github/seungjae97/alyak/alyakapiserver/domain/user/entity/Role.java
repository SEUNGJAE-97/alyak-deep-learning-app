package com.github.seungjae97.alyak.alyakapiserver.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "role")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Role {

    @Id
    @Column(name = "role_id")
    private Integer id;

    @Column(name = "role_name", nullable = false)
    private String name;
}