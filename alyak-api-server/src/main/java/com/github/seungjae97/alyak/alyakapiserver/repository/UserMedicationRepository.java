package com.github.seungjae97.alyak.alyakapiserver.repository;

import com.github.seungjae97.alyak.alyakapiserver.entity.UserMedication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserMedicationRepository extends JpaRepository<UserMedication, Long> {
    List<UserMedication> findByUserId(Long userId);
    List<UserMedication> findByPillId(Long pillId);
} 