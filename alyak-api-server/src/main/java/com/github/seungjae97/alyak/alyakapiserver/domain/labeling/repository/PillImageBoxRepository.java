package com.github.seungjae97.alyak.alyakapiserver.domain.labeling.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.entity.PillImageBox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PillImageBoxRepository extends JpaRepository<PillImageBox, Long> {
}
