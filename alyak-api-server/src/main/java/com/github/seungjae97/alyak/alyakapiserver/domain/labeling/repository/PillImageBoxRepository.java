package com.github.seungjae97.alyak.alyakapiserver.domain.labeling.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.entity.PillImageBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PillImageBoxRepository extends JpaRepository<PillImageBox, Long> {
    @Modifying
    @Query("DELETE FROM PillImageBox b WHERE b.imageData.id = :imageId")
    void deleteAllByImageDataId(@Param("imageId") Long imageId);
}
