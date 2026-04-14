package com.fof.demo.service;

import com.fof.demo.dto.AIResponse;
import com.fof.demo.dto.AISaleDTO;
import com.fof.demo.dto.AISalesRequest;
import com.fof.demo.entity.SaleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List; // Liste de ventes
import java.util.stream.Collectors; // Conversion en DTO

@Service // Rend la classe injectable par Spring
@RequiredArgsConstructor // Génère le constructeur avec les champs final
public class AIAnalysisService { // Service métier de l'analyse IA

    private final SaleService saleService; // Service des ventes
    private final WebClient webClient; // Client HTTP vers le microservice FastAPI

    public AIResponse analyzeSales() { // Lance l'analyse IA

        List<SaleEntity> sales = saleService.findAllEntitiesInBatches(500); // Charge les ventes par lots

        List<AISaleDTO> aiSales = sales.stream() // Transforme la liste en flux
                .map(s -> new AISaleDTO( // Convertit chaque vente en DTO IA
                        s.getSaleDate(), // Date de vente
                        s.getProduct(), // Produit
                        s.getPrice(), // Prix
                        s.getQuantity(), // Quantité
                        s.getStock() // Stock
                ))
                .collect(Collectors.toList()); // Liste finale pour l'IA

        AISalesRequest request = new AISalesRequest(aiSales); // Construit le payload envoyé à FastAPI

        AIResponse response = webClient.post() // Prépare une requête POST
                .uri("/api/analyze") // Endpoint du microservice IA
                .bodyValue(request) // Envoie le corps JSON
                .retrieve() // Exécute la requête
                .bodyToMono(AIResponse.class) // Convertit la réponse en AIResponse
                .onErrorReturn(new AIResponse()) // Retourne une réponse vide si FastAPI est indisponible
                .block(); // Attend le résultat de façon synchrone

        return response; // Renvoie la réponse IA
    }
}