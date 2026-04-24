package com.github.seungjae97.alyak.alyakapiserver.domain.training.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.training.entity.TrainingJob;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.entity.TrainingJobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainingJobRepository extends JpaRepository<TrainingJob, Long> {
    List<TrainingJob> findByStatus(TrainingJobStatus status);
}
