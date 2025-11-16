package com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity;

import jakarta.persistence.*;
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

    @Column(name = "pill_description", columnDefinition = "TEXT")
    @Lob
    private String pillDescription;

    @Column(name = "user_method", columnDefinition = "TEXT")
    @Lob
    private String userMethod;

    @Column(name = "pill_efficacy", columnDefinition = "TEXT")
    @Lob
    private String pillEfficacy;

    @Column(name = "pill_warn", columnDefinition = "TEXT")
    @Lob
    private String pillWarn;

    @Column(name = "pill_caution", columnDefinition = "TEXT")
    @Lob
    private String pillCaution;

    @Column(name = "pill_interactive", columnDefinition = "TEXT")
    @Lob
    private String pillInteractive;

    @Column(name = "pill_adverse_reaction", columnDefinition = "TEXT")
    @Lob
    private String pillAdverseReaction;

    @Column(name = "pill_manufacturer")
    private String pillManufacturer;

    @Column(name = "pill_img")
    private String pillImg;

    @Column(name = "pill_ingredient")
    private String pillIngredient;
}
