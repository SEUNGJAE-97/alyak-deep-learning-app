package com.github.seungjae97.alyak.alyakapiserver.domain.labeling.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.entity.PillImageData;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.entity.DataStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PillImageDataRepository extends JpaRepository<PillImageData, Long> {
    Page<PillImageData> findByStatus(DataStatus status, Pageable pageable);
}
