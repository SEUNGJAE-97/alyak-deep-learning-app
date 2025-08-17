package com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity;

import com.github.seungjae97.alyak.alyakapiserver.domain.medication.entity.UserMedication;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pills")
@Getter
@Setter
@NoArgsConstructor
public class Pills {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String pillName;
    
    @Column
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pill_shape_id")
    private PillShapes pillShape;
    
    @Column
    private String manufacturer;
    
    // 양방향 관계 설정
    @OneToMany(mappedBy = "pill", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserMedication> userMedications = new ArrayList<>();
} 