package com.github.seungjae97.alyak.alyakapiserver.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pill_shapes")
@Getter
@Setter
@NoArgsConstructor
public class PillShapes {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String shapeName;
    
    @Column
    private String description;
    
    // 양방향 관계 설정
    @OneToMany(mappedBy = "pillShape", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pills> pills = new ArrayList<>();
} 