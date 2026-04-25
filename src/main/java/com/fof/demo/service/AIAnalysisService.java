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

/**
 * Service métier responsable de l'analyse des données de ventes via un microservice IA.
 *
 * <p>
 * Ce service agit comme une passerelle entre le backend Spring Boot et
 * un microservice externe basé sur FastAPI.
 * </p>
 *
 * <p>
 * Responsabilités principales :
 * <ul>
 *     <li>Récupérer les données de ventes depuis la base</li>
 *     <li>Transformer les entités en DTO compatibles avec le service IA</li>
 *     <li>Construire la requête d'analyse</li>
 *     <li>Appeler le microservice IA via HTTP</li>
 *     <li>Retourner les résultats d'analyse (prédictions, anomalies, recommandations, etc.)</li>
 * </ul>
 * </p>
 *
 * <p>
 * Ce service est conçu pour s'intégrer dans une architecture microservices
 * avec séparation claire entre logique métier et traitement IA.
 * </p>
 *
 * @author Fodeba Fofana
 */
@Service
@RequiredArgsConstructor
public class AIAnalysisService {

    /**
     * Service permettant l'accès aux données de ventes.
     */
    private final SaleService saleService;

    /**
     * Client HTTP utilisé pour communiquer avec le microservice IA (FastAPI).
     */
    private final WebClient webClient;

    /**
     * Lance une analyse complète des ventes via le microservice IA.
     *
     * <p>
     * Étapes de traitement :
     * <ul>
     *     <li>Chargement des ventes par lots pour optimiser la mémoire</li>
     *     <li>Transformation des entités en DTO adaptés à l'IA</li>
     *     <li>Envoi des données au microservice FastAPI</li>
     *     <li>Récupération et mapping de la réponse IA</li>
     * </ul>
     * </p>
     *
     * <p>
     * En cas d'erreur (ex : microservice indisponible), une réponse vide est retournée
     * afin d'assurer la résilience du système.
     * </p>
     *
     * @return un objet {@link AIResponse} contenant les résultats d'analyse IA
     */
    public AIResponse analyzeSales() {

        // Chargement des ventes en batch pour éviter les problèmes de performance/mémoire
        List<SaleEntity> sales = saleService.findAllEntitiesInBatches(500);

        // Transformation des entités en DTO envoyables au microservice IA
        List<AISaleDTO> aiSales = sales.stream()
                .map(s -> new AISaleDTO(
                        s.getSaleDate(),
                        s.getProduct(),
                        s.getPrice(),
                        s.getQuantity(),
                        s.getStock()
                ))
                .collect(Collectors.toList());

        // Construction de la requête IA
        AISalesRequest request = new AISalesRequest(aiSales);

        // Appel du microservice FastAPI
        AIResponse response = webClient.post()
                .uri("/api/analyze")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AIResponse.class)
                .onErrorReturn(new AIResponse()) // fallback en cas d'erreur
                .block(); // appel synchrone

        return response;
    }
}