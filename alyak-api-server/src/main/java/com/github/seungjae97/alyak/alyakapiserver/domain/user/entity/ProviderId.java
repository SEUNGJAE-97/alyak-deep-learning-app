package com.github.seungjae97.alyak.alyakapiserver.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProviderId implements Serializable {
    @Column(name = "provider_name")
    private String providerName;

    @Column(name = "user_id")
    private Long userId;
}
