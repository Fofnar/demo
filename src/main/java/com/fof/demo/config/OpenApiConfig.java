package com.fof.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration OpenAPI (Swagger).
 *
 * Cette configuration permet :
 * - de documenter l'API backend Felyxor ;
 * - d'intégrer l'authentification JWT dans Swagger ;
 * - de fournir une description claire du produit pour les développeurs et recruteurs.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()

                /*
                 * Déclare l'utilisation du schéma de sécurité JWT
                 * pour les endpoints protégés.
                 */
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))

                .components(new Components()

                        /*
                         * Configuration du mécanisme d'authentification Bearer JWT.
                         */
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer") // Doit être en minuscule pour compatibilité Swagger
                                        .bearerFormat("JWT")
                        )
                )

                /*
                 * Informations globales de l'API.
                 * Cette section est visible dans Swagger UI.
                 */
                .info(new Info()
                        .title("Felyxor API — Intelligent Business Intelligence Platform")
                        .version("1.0.0")
                        .description("""
                                Felyxor is an AI-powered business intelligence platform designed to transform operational data into actionable insights.

                                Key capabilities:
                                - Secure authentication (JWT access + refresh tokens)
                                - Sales and user management (CRUD, pagination, filtering, dynamic search)
                                - AI-driven analytics:
                                    • Anomaly detection
                                    • Sales forecasting
                                    • Business health scoring
                                    • Inventory analysis
                                    • Stockout prediction
                                    • Intelligent recommendations

                                The API serves as the core engine powering the Felyxor analytics platform.
                                """)
                );
    }
}