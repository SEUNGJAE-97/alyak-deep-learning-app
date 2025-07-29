package com.github.seungjae97.alyak.alyakapiserver.pill.repository;

import com.github.seungjae97.alyak.alyakapiserver.pill.entity.Pills;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PillsRepository extends JpaRepository<Pills, Long> {
    List<Pills> findByPillShapeId(Long pillShapeId);
} 