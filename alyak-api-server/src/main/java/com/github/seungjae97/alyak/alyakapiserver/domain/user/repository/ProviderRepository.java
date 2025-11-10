package com.github.seungjae97.alyak.alyakapiserver.domain.user.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderRepository extends JpaRepository<Provider, Long> {
}
