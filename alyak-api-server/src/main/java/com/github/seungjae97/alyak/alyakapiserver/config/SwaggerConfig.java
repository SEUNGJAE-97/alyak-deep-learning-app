package com.github.seungjae97.alyak.alyakapiserver.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Alyak API Server")
                        .description("Alyak 약물 관리 시스템 API 서버")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Alyak Team")
                                .email("contact@alyak.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Server"),
                        new Server().url("https://api.alyak.com").description("Production Server")
                ));
    }
} 