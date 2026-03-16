package com.fof.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

// Classe de configuration Spring
@Configuration
public class WebClientConfig {

    // Création d'un Bean WebClient disponible dans toute l'application
    @Bean
    public WebClient webClient(){

        //builder() permet de configurer le client HTTP
        return WebClient.builder()

                //URL de base du microservice FastAPI
                .baseUrl("http://localhost:8000")

                //construit l'objet Webclient
                .build();
    }
}
