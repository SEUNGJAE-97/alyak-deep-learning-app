package com.github.seungjae97.alyak.alyakapiserver.domain.pill.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.Pill;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;


public interface PillRepository extends JpaRepository<Pill, Long> , PillRepositoryCustom {

    List<Pill> findByPillName(String pillName);
}
