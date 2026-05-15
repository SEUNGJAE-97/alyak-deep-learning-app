package com.github.seungjae97.alyak.alyakapiserver.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.Role;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.RoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import support.QueryCounter;
import support.RepositoryTestBase;

import static org.assertj.core.api.Assertions.assertThat;

class RoleRepositoryTest extends RepositoryTestBase {

    @Autowired private RoleRepository roleRepository;

    @Test
    @DisplayName("저장 후 ID로 조회 시 데이터가 일치한다")
    void save_and_findById() {
        Role saved = roleRepository.save(Role.builder().id(100).name("ADMIN").build());
        flushAndClear();
        QueryCounter.reset();

        Role found = roleRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getName()).isEqualTo("ADMIN");
        assertQueryCount(1);
    }

    @Test
    @DisplayName("존재하지 않는 ID 조회 시 empty를 반환한다")
    void findById_notFound() {
        assertNotFound(roleRepository.findById(999_999));
    }

    @Test
    @DisplayName("role_name 누락 시 저장에 실패한다")
    void save_withoutName_fails() {
        assertDataIntegrityViolation(() -> roleRepository.saveAndFlush(
                Role.builder().id(200).build()
        ));
    }
}
