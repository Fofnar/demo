package com.fof.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

// Classe de configuration Spring
@Configuration
public class WebClientConfig {

    // Création d'un Bean WebClient disponible dans toute l'application
    @Bean
    public WebClient webClient(@Value("${ml.service.url}") String baseUrl){



        //builder() permet de configurer le client HTTP
        return WebClient.builder()

                //URL de base du microservice FastAPI
                .baseUrl(baseUrl)

                //construit l'objet Webclient
                .build();
    }
}
