package com.github.seungjae97.alyak.alyakapiserver.domain.pill.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.PillAppearance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface PillAppearanceRepository extends JpaRepository<PillAppearance, Long> {
    @Query("""
        SELECT a FROM PillAppearance a
        JOIN FETCH a.pill
        WHERE LOWER(a.pillFront) LIKE LOWER(CONCAT('%', :text, '%'))
           OR LOWER(a.pillBack)  LIKE LOWER(CONCAT('%', :text, '%'))
    """)
    List<PillAppearance> findByPillTextWithPill(@Param("text") String text);
}
