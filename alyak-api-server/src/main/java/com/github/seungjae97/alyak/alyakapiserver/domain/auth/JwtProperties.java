package com.github.seungjae97.alyak.alyakapiserver.domain.auth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {
    
    private String secret;
    private long expirationTime = 1000 * 60 * 60; // 기본값: 1시간
    private String header = "Authorization";
    private String prefix = "Bearer ";
}
