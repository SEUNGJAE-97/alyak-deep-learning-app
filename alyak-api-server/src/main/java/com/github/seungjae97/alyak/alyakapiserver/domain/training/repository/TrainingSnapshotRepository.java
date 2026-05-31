package com.github.seungjae97.alyak.alyakapiserver.domain.training.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.training.entity.TrainingSnapshot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingSnapshotRepository extends JpaRepository<TrainingSnapshot, Long> {
    
    Page<TrainingSnapshot> findAllByTrainingJobId(Long trainingJobId, Pageable pageable);
}
