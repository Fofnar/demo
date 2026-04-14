package com.fof.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * Config Swagger
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("Bearer")
                                        .bearerFormat("JWT")
                        )
                )
                .info(new Info()
                        .title("AI Sales Analytics API")
                        .version("1.0")
                        .description("""
                                🚀 Intelligent Sales Analytics API
                                
                                Features
                                - Secure authentication: JWT (access + refresh)
                                - User & Sales Management: CRUD, pagination, sorting, filters, dynamic search (Specifications)
                                - AI Analysis (anomalies, predictions, recommendations, health Score, Sales indicator)
                                """)
                );
    }
}