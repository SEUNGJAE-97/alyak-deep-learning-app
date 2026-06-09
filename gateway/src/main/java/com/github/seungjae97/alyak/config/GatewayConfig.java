package com.github.seungjae97.alyak.config;

import com.github.seungjae97.alyak.filter.AuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    private final AuthFilter authFilter;


    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("springboot-api", r -> r
                        .path("/api/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("http://spring-boot-service:8080"))

                .route("fastapi-api", r -> r
                        .path("/ml/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("http://fastapi-service:8000"))

                .route("sse-stream", r -> r
                        .path("/stream/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("http://fastapi-service:8000"))

                .build();
    }
}
