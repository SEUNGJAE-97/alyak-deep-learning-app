package com.github.seungjae97.alyak.alyakapiserver.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    private Gender gender;
    
    @Column(name = "resident_registration_number")
    private String residentRegistrationNumber;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    // 양방향 관계 설정
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<com.github.seungjae97.alyak.alyakapiserver.family.entity.FamilyMember> familyMembers = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<com.github.seungjae97.alyak.alyakapiserver.medication.entity.UserMedication> userMedications = new ArrayList<>();
    
    public enum Gender {
        M, F
    }
} 