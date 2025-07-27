package com.github.seungjae97.alyak.alyakapiserver.repository;

import com.github.seungjae97.alyak.alyakapiserver.entity.PillShapes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PillShapesRepository extends JpaRepository<PillShapes, Long> {
    // 기본 CRUD 메서드들은 JpaRepository에서 자동 제공
} 