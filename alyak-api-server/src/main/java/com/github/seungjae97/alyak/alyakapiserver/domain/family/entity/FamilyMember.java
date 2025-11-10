package com.github.seungjae97.alyak.alyakapiserver.domain.family.entity;

import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "family_members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class FamilyMember {

    @EmbeddedId
    private FamilyMemberId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("familyId")
    @JoinColumn(name = "family_id")
    private Family family;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;
}