package com.github.seungjae97.alyak.alyakapiserver.domain.pill.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.Pill;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.PillAppearance;
import com.github.seungjae97.alyak.alyakapiserver.global.common.repository.JDBCRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PillAppearanceRepository extends JpaRepository<PillAppearance, Long> {
}
