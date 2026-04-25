package com.fof.demo.controller;

import com.fof.demo.dto.AIResponse;
import com.fof.demo.dto.ApiResponse;
import com.fof.demo.service.AIAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Contrôleur REST dédié à l'analyse intelligente des ventes.
 *
 * Ce contrôleur expose un endpoint permettant de :
 * - lancer l'analyse IA des ventes
 * - récupérer les résultats d'analyse sous forme structurée
 *
 * L'endpoint principal interroge le microservice FastAPI
 * et retourne un objet API standardisé via ApiResponse.
 *
 * Ce type de contrôleur est généralement protégé
 * par un système d'authentification (JWT) et
 * accessible uniquement aux administrateurs.
 *
 * @author Fodeba Fofana
 */

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    /**
     * Service métier chargé de lancer l'analyse IA.
     */
    private final AIAnalysisService aiService;

    /**
     * Lance l'analyse intelligente des ventes.
     *
     * Ce endpoint :
     * - récupère les ventes depuis la base de données
     * - envoie les données au microservice FastAPI
     * - retourne les résultats IA dans une réponse standardisée
     *
     * @return réponse contenant les résultats d'analyse IA
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/analysis")
    @Operation(
            summary = "Run AI sales analysis",
            description = "Triggers the AI analysis pipeline and returns business insights such as anomalies, predictions, recommendations, health score and stock analysis."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Analysis completed successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid request"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<ApiResponse<AIResponse>> analyze() {

        // Appelle le service qui interroge le microservice FastAPI
        AIResponse aiResponse = aiService.analyzeSales();

        ApiResponse<AIResponse> response = new ApiResponse<>(
                true,
                aiResponse,
                "Analysis completed successfully",
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }
}