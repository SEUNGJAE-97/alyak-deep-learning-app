package com.github.seungjae97.alyak.alyakapiserver.domain.medication.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.medication.entity.UserMedication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMedicationRepository extends JpaRepository<UserMedication, Long> {
    List<UserMedication> findByUserId(Long userId);
} 