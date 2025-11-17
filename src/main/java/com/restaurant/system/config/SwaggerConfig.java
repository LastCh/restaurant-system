package com.restaurant.system.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .title("Restaurant Management System API")
                        .version("1.0")
                        .description("REST API for managing restaurant"))

                // Определяем JWT Security Scheme
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token. Пример: Bearer eyJhbGc...")
                        )
                )

                // Глобально требуем Bearer токен для всех эндпоинтов
                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer Authentication")
                );
    }
}
