package com.github.seungjae97.alyak.alyakapiserver.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.family.entity.Family;
import com.github.seungjae97.alyak.alyakapiserver.domain.family.entity.FamilyMember;
import com.github.seungjae97.alyak.alyakapiserver.domain.family.repository.FamilyMemberRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.family.repository.FamilyRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import support.QueryCounter;
import support.RepositoryTestBase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class FamilyMemberRepositoryTest extends RepositoryTestBase {

    private static final int MEMBER_COUNT = 10;

    @Autowired private FamilyMemberRepository familyMemberRepository;
    @Autowired private FamilyRepository familyRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private EntityManager entityManager;

    private Long familyId;
    private Long firstUserId;

    @BeforeEach
    void setUp() {
        // 1. 테스트 데이터 준비
        Family family = familyRepository.save(Family.builder().build());
        familyId = family.getId();

        for (int i = 0; i < MEMBER_COUNT; i++) {
            User user = userRepository.save(User.builder()
                    .email("member" + i + "@test.com")
                    .name("멤버" + i)
                    .build());
            if (i == 0) firstUserId = user.getUserId();

            familyMemberRepository.save(FamilyMember.builder()
                    .user(user)
                    .family(family)
                    .build());
        }

        // 2. 영속성 컨텍스트 초기화 (실제 DB 쿼리 발생을 위해 필수)
        entityManager.flush();
        entityManager.clear();

        // 3. 카운터 초기화
        QueryCounter.reset();
    }

    @Test
    @DisplayName("가족 ID로 모든 멤버를 조회한다")
    void findAllByFamilyWithUser() {
        List<FamilyMember> members = familyMemberRepository.findAllByFamilyWithUser(familyId);

        assertThat(members).hasSize(MEMBER_COUNT);
    }

    @Test
    @DisplayName("가족 멤버 여부 확인 - 존재하는 경우")
    void exists_True() {
        boolean exists = familyMemberRepository.existsByUser_UserIdAndFamily_Id(firstUserId, familyId);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("가족 멤버 여부 확인 - 존재하지 않는 경우")
    void exists_False() {
        User outsider = userRepository.save(User.builder()
                .email("outsider@test.com")
                .name("외부인")
                .build());

        boolean exists = familyMemberRepository.existsByUser_UserIdAndFamily_Id(outsider.getUserId(), familyId);

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("가족 ID로 조회 시 연관된 User 접근에 따른 N+1이 없어야 한다")
    void findAllByFamily_WithUser_No_NPlusOne() {
        // when: 가족 멤버 조회 (Query 1: FamilyMember 조회)
        List<FamilyMember> members = familyMemberRepository.findAllByFamilyWithUser(familyId);

        // then: User 프록시 초기화 (Query 2: Batch Size에 의한 User 일괄 조회)
        members.forEach(member -> member.getUser().getName());

        assertThat(QueryCounter.getCount())
                .as("쿼리는 한번만 날아가야함")
                .isEqualTo(1);
    }

    @Test
    @DisplayName("exists 쿼리는 단건만 발생해야 한다")
    void exists_Query_Single() {
        familyMemberRepository.existsByUser_UserIdAndFamily_Id(firstUserId, familyId);

        assertThat(QueryCounter.getCount())
                .as("exists 쿼리는 1번만 발생해야 함")
                .isEqualTo(1);
    }
}