package com.github.seungjae97.alyak.alyakapiserver.domain.user.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.UserRole;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.UserRoleId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
    /**
     * 특정 사용자의 모든 역할 조회 (Role 엔티티도 함께 로드)
     * @param userId 사용자 ID
     * @return 해당 사용자의 역할 목록
     */
    @EntityGraph(attributePaths = {"role"})
    List<UserRole> findByUser_Id(Long userId);


    void deleteByUserId(Long userId);
}

