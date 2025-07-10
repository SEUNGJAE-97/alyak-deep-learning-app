package com.github.seungjae97.alyak.alyakapiserver.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
public class FamilyMemberDto {
    private int id;
    private String familyId;
    private int userId;
    private char role;
    private Timestamp joinedAt;
}
