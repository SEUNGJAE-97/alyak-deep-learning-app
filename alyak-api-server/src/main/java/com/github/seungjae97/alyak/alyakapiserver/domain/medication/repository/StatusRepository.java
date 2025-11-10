package com.github.seungjae97.alyak.alyakapiserver.domain.medication.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.medication.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {
}
