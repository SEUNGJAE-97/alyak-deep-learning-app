package com.github.seungjae97.alyak.alyakapiserver.domain.user.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    @Query("""
        SELECT DISTINCT u
        FROM User u
        JOIN FETCH u.userRole ur
        JOIN FETCH ur.role
        WHERE u.email = :email
    """)
    Optional<User> findByEmailWithRoles(@Param("email") String email);
    boolean existsByEmail(String email);
} 