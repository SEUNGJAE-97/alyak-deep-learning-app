package com.github.seungjae97.alyak.alyakapiserver.domain.user.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
}
