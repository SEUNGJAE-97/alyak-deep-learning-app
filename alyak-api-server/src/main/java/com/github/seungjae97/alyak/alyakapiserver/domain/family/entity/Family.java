package com.github.seungjae97.alyak.alyakapiserver.domain.family.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "families")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Family {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "family_id")
    private Long id;

    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FamilyMember> members = new ArrayList<>();

    @Builder
    public Family(Long id, List<FamilyMember> members) {
        this.id = id;
        this.members = members;
    }
}