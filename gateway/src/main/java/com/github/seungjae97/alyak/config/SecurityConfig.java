package com.github.seungjae97.alyak.config;

import com.github.seungjae97.alyak.filter.AuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthFilter authFilter;

    private static final String[] PUBLIC_URLS = {
            "/api/auth/**",
            "/api/internal/training/**",
            "/api/internal/labeling/**",
            "/uploads/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api-docs/**",
            "/v3/api-docs/**",
            "/actuator/**",
            "/api/email/**",
            "/auth/kakao/**",
            "/auth/google/**",
            "/error"
    };

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
       return http
               .csrf(ServerHttpSecurity.CsrfSpec::disable)
               .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
               .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
               .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
               .cors(cors -> cors.configurationSource(corsConfigurationSource()))
               .authorizeExchange(exchange -> exchange
                       .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                       .pathMatchers(PUBLIC_URLS).permitAll()
                       .anyExchange().authenticated()
               )
               .addFilterAt(authFilter, SecurityWebFiltersOrder.AUTHENTICATION)

               .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:8080",
                "http://localhost:8000"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
