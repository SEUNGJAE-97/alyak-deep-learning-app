package com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Table(name = "pill")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Pill {

    @Id
    @Column(name = "pill_id")
    private Long id;

    @Column(name = "pill_name")
    private String pillName;

    @Column(name = "pill_description")
    private String pillDescription;

    @Column(name = "user_method")
    private String userMethod;

    @Column(name = "pill_efficacy")
    private String pillEfficacy;

    @Column(name = "pill_warn")
    private String pillWarn;

    @Column(name = "pill_caution")
    private String pillCaution;

    @Column(name = "pill_interactive")
    private String pillInteractive;

    @Column(name = "pill_adverse_reaction")
    private String pillAdverseReaction;

    @Column(name = "pill_manufacturer")
    private String pillManufacturer;

    @Column(name = "pill_img")
    private String pillImg;

    @Column(name = "pill_ingredient")
    private String pillIngredient;
}
