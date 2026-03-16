package com.fof.demo.service;

import com.fof.demo.dto.AIResponse;
import com.fof.demo.dto.AISaleDTO;
import com.fof.demo.dto.AISalesRequest;
import com.fof.demo.entity.SaleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AIAnalysisService {
    // Service permettant de récupérer les ventes dans la base
    private final SaleService saleService;

    // Client HTTP pour appeler FastAPI
    private final WebClient webClient;

    public AIResponse analyzeSales(){

        //Récupérer toutes les ventes depuis la base
        List<SaleEntity> sales = saleService.findAll();

        //convertir les entité DB en DTO pour l'IA
        List<AISaleDTO> aiSales = sales.stream()
                .map(s-> new AISaleDTO(
                        s.getSaleDate(),
                        s.getProduct(),
                        s.getPrice(),
                        s.getQuantity(),
                        s.getStock()
                ))
                .collect(Collectors.toList());
        // 3️⃣ créer la requête envoyée au service IA
        AISalesRequest request = new AISalesRequest(aiSales);

        //appel HTTP vers FastAPI avec WebClient
        AIResponse response = webClient.post()

                // endpoint du microservice IA
                .uri("/api/analyze")

                // body de la requête
                .bodyValue(request)

                // envoyer la requête
                .retrieve()

                // convertir la réponse JSON en objet AIResponse
                .bodyToMono(AIResponse.class)

                //Sécuriser l'appel si FastAPI tombe sur "connection refused"
                .onErrorReturn(new AIResponse())

                //block() attend la réponse (version simple)
                .block();

        return response;
    }
}