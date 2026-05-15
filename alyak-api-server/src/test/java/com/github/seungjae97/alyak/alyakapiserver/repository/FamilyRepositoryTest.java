package com.github.seungjae97.alyak.alyakapiserver.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.family.entity.Family;
import com.github.seungjae97.alyak.alyakapiserver.domain.family.entity.FamilyMember;
import com.github.seungjae97.alyak.alyakapiserver.domain.family.repository.FamilyMemberRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.family.repository.FamilyRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import support.QueryCounter;
import support.RepositoryTestBase;
import support.RepositoryTestFixtures;

import static org.assertj.core.api.Assertions.assertThat;

class FamilyRepositoryTest extends RepositoryTestBase {

    @Autowired private FamilyRepository familyRepository;
    @Autowired private FamilyMemberRepository familyMemberRepository;
    @Autowired private UserRepository userRepository;

    @Test
    @DisplayName("저장 후 ID가 생성되고 조회 시 데이터가 일치한다")
    void save_and_findById() {
        Family saved = familyRepository.save(Family.builder().build());
        flushAndClear();
        QueryCounter.reset();

        Family found = familyRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getId()).isEqualTo(saved.getId());
        assertQueryCount(1);
    }

    @Test
    @DisplayName("존재하지 않는 ID 조회 시 empty를 반환한다")
    void findById_notFound() {
        assertNotFound(familyRepository.findById(999_999L));
    }

    @Test
    @DisplayName("가족 삭제 시 CASCADE로 FamilyMember가 삭제된다")
    void delete_cascadesToFamilyMembers() {
        Family family = familyRepository.save(Family.builder().build());
        familyMemberRepository.save(FamilyMember.builder()
                .family(family)
                .user(userRepository.save(RepositoryTestFixtures.user("cascade")))
                .build());
        flushAndClear();

        familyRepository.deleteById(family.getId());
        flushAndClear();

        assertThat(familyMemberRepository.findAllByFamilyWithUser(family.getId())).isEmpty();
    }
}
