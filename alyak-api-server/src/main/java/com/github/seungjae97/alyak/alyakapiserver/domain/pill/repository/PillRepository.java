package com.github.seungjae97.alyak.alyakapiserver.domain.pill.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.Pill;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;


public interface PillRepository extends JpaRepository<Pill, Long> , PillRepositoryCustom {

    List<Pill> findByPillName(String pillName);
    // PillRepository 수정
    Page<Pill> findByPillNameContaining(String keyword, Pageable pageable);
}
