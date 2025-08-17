package com.github.seungjae97.alyak.alyakapiserver.domain.family.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.family.entity.Families;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FamiliesRepository extends JpaRepository<Families, Long> {
} 