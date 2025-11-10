package com.github.seungjae97.alyak.alyakapiserver.domain.user.entity;

import com.github.seungjae97.alyak.alyakapiserver.domain.family.entity.FamilyMember;
import com.github.seungjae97.alyak.alyakapiserver.domain.medication.entity.MedicationSchedule;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FamilyMember> familyMembers = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicationSchedule> medicationSchedules;

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public enum Provider {
        LOCAL,
        GOOGLE,
        KAKAO
    }
}