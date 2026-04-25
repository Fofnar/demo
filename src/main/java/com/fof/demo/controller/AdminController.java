package com.fof.demo.controller;

import com.fof.demo.dto.AdminStatsDTO;
import com.fof.demo.dto.ApiResponse;
import com.fof.demo.service.AdminService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * REST Controller dédié aux statistiques administrateur.
 *
 * Ce contrôleur expose un endpoint permettant de :
 * - récupérer les statistiques globales de l'application
 *
 * Toutes les routes de ce contrôleur sont préfixées par :
 *
 *      /api/admin
 *
 * Ce type de contrôleur est généralement protégé
 * par un système d'authentification (JWT) et
 * accessible uniquement aux administrateurs.
 *
 * @author Fodeba Fofana
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    /**
     * Service métier responsable du calcul des statistiques.
     */
    private final AdminService adminService;

    /**
     * Récupère les statistiques globales de l'application.
     *
     * Les statistiques retournées peuvent inclure :
     * - nombre total d'utilisateurs
     * - nombre total de ventes
     * - ventes du jour
     * - nombre de produits distincts
     * - nombre de produits avec stock faible
     *
     * @return réponse contenant les statistiques globales
     */
    @GetMapping("/stats")
    @Operation(
            summary = "Get admin statistics",
            description = "Retrieves global application statistics such as total users, total sales, today's sales, total products and low stock products."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Statistics retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<ApiResponse<AdminStatsDTO>> stats() {
        AdminStatsDTO stats = adminService.getStats();

        ApiResponse<AdminStatsDTO> response = new ApiResponse<>(
                true,
                stats,
                "Statistics retrieved successfully",
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }
}