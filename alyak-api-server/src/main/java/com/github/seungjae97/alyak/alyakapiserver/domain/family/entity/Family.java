package com.github.seungjae97.alyak.alyakapiserver.domain.family.entity;

import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Table(name = "family")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Family {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "family_id")
    @Id
    private Long id;

    @OneToMany(mappedBy = "family")
    private List<User> users;

}
