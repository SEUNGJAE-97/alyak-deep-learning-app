package com.github.seungjae97.alyak.alyakapiserver.domain.pill.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.PillShape;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PillShapesRepository extends JpaRepository<PillShape, Long> {
} 