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
import support.RepositoryTestBase;
import support.RepositoryTestFixtures;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserRoleRepositoryTest extends RepositoryTestBase {

    @Autowired private UserRoleRepository userRoleRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;

    private Long userId;
    private Integer roleId;

    @BeforeEach
    void setUp() {
        User user = userRepository.save(RepositoryTestFixtures.user("role"));
        Role role = roleRepository.save(Role.builder().id(10).name("USER").build());
        userId = user.getUserId();
        roleId = role.getId();

        userRoleRepository.save(UserRole.builder()
                .id(new UserRoleId(userId, roleId))
                .user(user)
                .role(role)
                .build());
        flushAndClear();
    }

    @Test
    @DisplayName("저장 후 복합키로 조회 시 데이터가 일치한다")
    void save_and_findById() {
        UserRole found = userRoleRepository.findById(new UserRoleId(userId, roleId)).orElseThrow();
        assertThat(found.getUser().getUserId()).isEqualTo(userId);
        assertThat(found.getRole().getId()).isEqualTo(roleId);
    }

    @Test
    @DisplayName("존재하지 않는 복합키 조회 시 empty를 반환한다")
    void findById_notFound() {
        assertNotFound(userRoleRepository.findById(new UserRoleId(999_999L, 999)));
    }

    @Test
    @DisplayName("EntityGraph로 Role을 함께 조회하면 N+1이 없다")
    void findByUser_userId_noNPlusOne() {
        List<UserRole> roles = userRoleRepository.findByUser_userId(userId);
        roles.forEach(ur -> ur.getRole().getName());

        assertQueryCount(1);
    }

    @Test
    @DisplayName("사용자 삭제 시 CASCADE로 UserRole이 삭제된다")
    void deleteUser_cascadesToUserRole() {
        userRepository.deleteById(userId);
        flushAndClear();

        assertThat(userRoleRepository.findByUser_userId(userId)).isEmpty();
    }

    @Test
    @DisplayName("deleteByUser_UserId로 사용자 역할을 일괄 삭제한다")
    void deleteByUser_UserId() {
        userRoleRepository.deleteByUser_UserId(userId);
        flushAndClear();

        assertThat(userRoleRepository.findByUser_userId(userId)).isEmpty();
    }
}
