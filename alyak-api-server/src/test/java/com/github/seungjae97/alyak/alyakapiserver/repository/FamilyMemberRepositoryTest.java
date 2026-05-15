package com.github.seungjae97.alyak.alyakapiserver.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.family.entity.Family;
import com.github.seungjae97.alyak.alyakapiserver.domain.family.entity.FamilyMember;
import com.github.seungjae97.alyak.alyakapiserver.domain.family.repository.FamilyMemberRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.family.repository.FamilyRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import support.QueryCounter;
import support.RepositoryTestBase;
import support.RepositoryTestFixtures;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FamilyMemberRepositoryTest extends RepositoryTestBase {

    private static final int MEMBER_COUNT = 10;

    @Autowired private FamilyMemberRepository familyMemberRepository;
    @Autowired private FamilyRepository familyRepository;
    @Autowired private UserRepository userRepository;

    private Long familyId;
    private Long firstUserId;

    @BeforeEach
    void setUp() {
        Family family = familyRepository.save(Family.builder().build());
        familyId = family.getId();

        for (int i = 0; i < MEMBER_COUNT; i++) {
            User user = userRepository.save(RepositoryTestFixtures.user(String.valueOf(i)));
            if (i == 0) {
                firstUserId = user.getUserId();
            }
            familyMemberRepository.save(FamilyMember.builder()
                    .user(user)
                    .family(family)
                    .build());
        }
        flushAndClear();
    }

    @Test
    @DisplayName("저장 후 ID가 생성되고 조회 시 데이터가 일치한다")
    void save_and_findById() {
        User user = userRepository.save(RepositoryTestFixtures.user("new"));
        Family family = familyRepository.save(Family.builder().build());
        flushAndClear();
        QueryCounter.reset();

        FamilyMember saved = familyMemberRepository.save(FamilyMember.builder()
                .user(user)
                .family(family)
                .build());
        flushAndClear();
        QueryCounter.reset();

        FamilyMember found = familyMemberRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getId()).isNotNull();
        assertThat(found.getUser().getUserId()).isEqualTo(user.getUserId());
        assertThat(found.getFamily().getId()).isEqualTo(family.getId());
    }

    @Test
    @DisplayName("존재하지 않는 ID 조회 시 empty를 반환한다")
    void findById_notFound() {
        assertNotFound(familyMemberRepository.findById(999_999L));
    }

    @Test
    @DisplayName("가족 ID로 모든 멤버를 조회한다")
    void findAllByFamilyWithUser() {
        List<FamilyMember> members = familyMemberRepository.findAllByFamilyWithUser(familyId);
        assertThat(members).hasSize(MEMBER_COUNT);
    }

    @Test
    @DisplayName("가족 멤버 여부 확인 - 존재하는 경우")
    void exists_true() {
        assertThat(familyMemberRepository.existsByUser_UserIdAndFamily_Id(firstUserId, familyId))
                .isTrue();
    }

    @Test
    @DisplayName("가족 멤버 여부 확인 - 존재하지 않는 경우")
    void exists_false() {
        User outsider = userRepository.save(RepositoryTestFixtures.user("outsider"));
        flushAndClear();

        assertThat(familyMemberRepository.existsByUser_UserIdAndFamily_Id(outsider.getUserId(), familyId))
                .isFalse();
    }

    @Test
    @DisplayName("fetch join 조회 후 User 접근 시 쿼리가 1회만 발생한다")
    void findAllByFamilyWithUser_noNPlusOne() {
        List<FamilyMember> members = familyMemberRepository.findAllByFamilyWithUser(familyId);
        members.forEach(member -> member.getUser().getName());

        assertQueryCount(1);
    }

    @Test
    @DisplayName("findAll 후 User 접근 시 N+1이 발생한다")
    void findAll_nPlusOne_when_accessingUser() {
        List<FamilyMember> members = familyMemberRepository.findAll();
        members.forEach(member -> member.getUser().getName());

        assertThat(QueryCounter.getCount()).isEqualTo(1 + MEMBER_COUNT);
    }

    @Test
    @DisplayName("exists 쿼리는 단건만 발생한다")
    void exists_singleQuery() {
        familyMemberRepository.existsByUser_UserIdAndFamily_Id(firstUserId, familyId);
        assertQueryCount(1);
    }

    @Test
    @DisplayName("가족 삭제 시 CASCADE로 멤버가 함께 삭제된다")
    void deleteFamily_cascadesToMembers() {
        familyRepository.deleteById(familyId);
        flushAndClear();

        assertThat(familyMemberRepository.findAllByFamilyWithUser(familyId)).isEmpty();
    }
}
