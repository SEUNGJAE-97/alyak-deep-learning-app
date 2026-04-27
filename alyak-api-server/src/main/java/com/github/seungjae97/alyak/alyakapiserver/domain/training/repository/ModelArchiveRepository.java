package com.github.seungjae97.alyak.alyakapiserver.domain.training.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.training.entity.ModelArchive;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.entity.ModelArchiveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ModelArchiveRepository extends JpaRepository<ModelArchive, Long> {
    Optional<ModelArchive> findByRunDir(String runDir);
    Optional<ModelArchive> findByVersion(String version);
    Page<ModelArchive> findByStatus(ModelArchiveStatus status, Pageable pageable);
}
