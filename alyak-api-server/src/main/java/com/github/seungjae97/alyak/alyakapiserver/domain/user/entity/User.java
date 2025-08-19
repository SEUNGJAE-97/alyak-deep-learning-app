package com.github.seungjae97.alyak.alyakapiserver.domain.user.entity;

import com.github.seungjae97.alyak.alyakapiserver.domain.family.entity.FamilyMember;
import com.github.seungjae97.alyak.alyakapiserver.domain.medication.entity.UserMedication;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column
    private String password;
    
    @Column(nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    private Gender gender;
    
    @Column(name = "resident_registration_number")
    private String residentRegistrationNumber;
    
    @Column(name = "phone_number")
    private String phoneNumber;

    @Column
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;
    
    // 양방향 관계 설정
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<FamilyMember> familyMembers = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserMedication> userMedications = new ArrayList<>();
    
    public enum Gender {
        M, F
    }

    public enum Provider {
        LOCAL,
        GOOGLE,
        KAKAO
    }

    public enum Role {
        Admin,
    }
} 