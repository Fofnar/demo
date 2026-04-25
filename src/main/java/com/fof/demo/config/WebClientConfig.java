package com.fof.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Client HTTP utilisé par le backend pour communiquer avec le microservice IA.
 *
 * L'URL du service IA est externalisée afin de permettre :
 * - un appel local vers FastAPI pendant le développement ;
 * - un appel vers l'URL Render du service IA en production.
 *
 * @author Fodeba Fofana
 */
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(@Value("${ml.service.url}") String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}