package com.github.seungjae97.alyak.alyakapiserver.domain.user.repository;

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
    
    /**
     * 특정 역할을 가진 모든 사용자 조회
     * @param roleId 역할 ID
     * @return 해당 역할을 가진 사용자 목록
     */
    List<UserRole> findByRole_Id(Integer roleId);
    
    /**
     * 특정 사용자와 역할로 조회
     * @param userId 사용자 ID
     * @param roleId 역할 ID
     * @return UserRole 엔티티
     */
    Optional<UserRole> findByUser_IdAndRole_Id(Long userId, Integer roleId);
}

