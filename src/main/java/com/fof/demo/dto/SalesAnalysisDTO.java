package com.fof.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO principal pour l'analyse des ventes.
 *
 * Ce bloc est généré par le microservice IA (FastAPI) et contient :
 * - des KPI globaux
 * - des indicateurs de performance
 * - une tendance business
 * - des recommandations exploitables
 *
 * Objectif :
 * Fournir une vue synthétique et actionnable au frontend (dashboard, pilotage métier).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // Sécurité si le JSON évolue côté IA
public class SalesAnalysisDTO {

    /**
     * Chiffre d'affaires total
     */
    @JsonProperty("total_revenue")
    private Double totalRevenue;

    /**
     * Quantité totale vendue
     */
    @JsonProperty("total_quantity_sold")
    private Integer totalQuantitySold;

    /**
     * Valeur moyenne d'une commande
     */
    @JsonProperty("average_order_value")
    private Double averageOrderValue;

    /**
     * Nombre de produits distincts
     */
    @JsonProperty("unique_products")
    private Integer uniqueProducts;

    /**
     * Nombre de jours distincts analysés
     */
    @JsonProperty("unique_days")
    private Integer uniqueDays;

    /**
     * Produit le plus vendu (volume)
     */
    @JsonProperty("top_selling_product")
    private String topSellingProduct;

    /**
     * Quantité vendue du produit le plus vendu
     */
    @JsonProperty("top_selling_quantity")
    private Double topSellingQuantity;

    /**
     * Produit générant le plus de revenu
     */
    @JsonProperty("top_revenue_product")
    private String topRevenueProduct;

    /**
     * Revenu généré par le produit le plus rentable
     */
    @JsonProperty("top_revenue_value")
    private Double topRevenueValue;

    /**
     * Tendance globale des ventes (upward, downward, stable)
     */
    @JsonProperty("trend")
    private String trend;

    /**
     * Pente de la tendance (issue d'une régression linéaire)
     */
    @JsonProperty("trend_slope")
    private Double trendSlope;

    /**
     * Liste des revenus journaliers (pour graphique)
     */
    @JsonProperty("revenue_per_day")
    private List<RevenuePerDayDTO> revenuePerDay;

    /**
     * Résumé business généré automatiquement par l'IA
     */
    @JsonProperty("sales_comment")
    private String salesComment;

    /**
     * Recommandations business actionnables
     */
    @JsonProperty("recommendations")
    private List<String> recommendations;
}