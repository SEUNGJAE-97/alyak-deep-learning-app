package com.github.seungjae97.alyak.alyakapiserver.pill.repository;

import com.github.seungjae97.alyak.alyakapiserver.pill.entity.PillShapes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PillShapesRepository extends JpaRepository<PillShapes, Long> {
} 