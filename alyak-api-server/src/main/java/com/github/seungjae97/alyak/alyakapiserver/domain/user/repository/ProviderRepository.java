package com.github.seungjae97.alyak.alyakapiserver.domain.user.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.Provider;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.ProviderId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProviderRepository extends JpaRepository<Provider, ProviderId> {
    Optional<Provider> findByUser_Id(Long userId);
}
