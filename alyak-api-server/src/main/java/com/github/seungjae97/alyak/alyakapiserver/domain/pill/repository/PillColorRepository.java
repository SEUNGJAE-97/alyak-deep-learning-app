package com.github.seungjae97.alyak.alyakapiserver.domain.pill.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.PillColor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PillColorRepository extends JpaRepository<PillColor, Long> {
    Optional<PillColor> findByColorName(String colorName);
}

