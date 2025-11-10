package com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity;

import com.github.seungjae97.alyak.alyakapiserver.domain.medication.entity.MedicationSchedule;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pills")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Pill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pill_id")
    private Long id;

    @Column(name = "pill_name", nullable = false)
    private String name;

    @Column(name = "pill_description")
    private String description;

    @Column(name = "use_method")
    private String useMethod;

    @Column(name = "efcy")
    private String efficiency;

    @Column(name = "warn")
    private String warning;

    @Column(name = "atpn")
    private String caution;

    @Column(name = "intrc")
    private String interaction;

    @Column(name = "se")
    private String sideEffect;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "pill_img")
    private String image;

    @OneToMany(mappedBy = "pill", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicationSchedule> medicationSchedules;
}
