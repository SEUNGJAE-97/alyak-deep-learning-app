package com.github.seungjae97.alyak.alyakapiserver.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.Role;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.UserRole;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.UserRoleId;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.RoleRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.UserRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.UserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import support.QueryCounter;
import support.RepositoryTestBase;
import support.RepositoryTestFixtures;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryTest extends RepositoryTestBase {

    @Autowired private UserRepository userRepository;
    @Autowired private UserRoleRepository userRoleRepository;
    @Autowired private RoleRepository roleRepository;

    private String email;

    @BeforeEach
    void setUp() {
        email = "repo-user@test.com";
        userRepository.save(RepositoryTestFixtures.user("repo").toBuilder().email(email).build());
        flushAndClear();
    }

    @Test
    @DisplayName("저장 후 ID가 생성되고 조회 시 데이터가 일치한다")
    void save_and_findById() {
        User saved = userRepository.save(RepositoryTestFixtures.user("save"));
        flushAndClear();
        QueryCounter.reset();

        User found = userRepository.findById(saved.getUserId()).orElseThrow();

        assertThat(found.getEmail()).isEqualTo(saved.getEmail());
        assertThat(found.getName()).isEqualTo(saved.getName());
        assertQueryCount(1);
    }

    @Test
    @DisplayName("존재하지 않는 ID 조회 시 empty를 반환한다")
    void findById_notFound() {
        assertNotFound(userRepository.findById(999_999L));
    }

    @Test
    @DisplayName("이메일로 사용자를 조회한다")
    void findByEmail() {
        User found = userRepository.findByEmail(email).orElseThrow();
        assertThat(found.getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("이메일 존재 여부를 확인한다")
    void existsByEmail() {
        assertThat(userRepository.existsByEmail(email)).isTrue();
        assertThat(userRepository.existsByEmail("not-exist@test.com")).isFalse();
    }

    @Test
    @DisplayName("findByEmailWithRoles는 Role까지 fetch join하여 N+1이 없다")
    void findByEmailWithRoles_noNPlusOne() {
        User user = userRepository.findByEmail(email).orElseThrow();
        Role role = roleRepository.save(Role.builder().id(1).name("USER").build());
        userRoleRepository.save(UserRole.builder()
                .id(new UserRoleId(user.getUserId(), role.getId()))
                .user(user)
                .role(role)
                .build());
        flushAndClear();
        QueryCounter.reset();

        User found = userRepository.findByEmailWithRoles(email).orElseThrow();
        found.getUserRole().forEach(ur -> ur.getRole().getName());

        assertQueryCount(1);
    }

    @Test
    @DisplayName("name 누락 시 저장에 실패한다")
    void save_withoutName_fails() {
        assertDataIntegrityViolation(() -> userRepository.saveAndFlush(
                User.builder().email("noname@test.com").build()
        ));
    }
}
