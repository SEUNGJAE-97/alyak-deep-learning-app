package com.github.seungjae97.alyak.alyakapiserver.domain.family.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamilyMemberId implements Serializable {
    private Long userId;
    private Long familyId;
}