package com.fof.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO principal pour le bloc "recommendations".
 *
 * Ce bloc regroupe :
 * - la tendance globale
 * - les produits clés
 * - les recommandations détaillées
 * - le résumé exécutif
 * - le forecast associé
 * - le contexte d'anomalies
 *
 * @author Fodeba Fofana
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecommendationsDTO {

    /**
     * Tendance globale détectée : upward, downward ou stable.
     */
    @JsonProperty("trend")
    private String trend;

    /**
     * Pente de la tendance linéaire.
     */
    @JsonProperty("trend_slope")
    private Double trendSlope;

    /**
     * Évolution relative entre le début et la fin de la période.
     */
    @JsonProperty("trend_change_rate")
    private Double trendChangeRate;

    /**
     * Produit le plus vendu en volume.
     */
    @JsonProperty("top_selling_product")
    private String topSellingProduct;

    /**
     * Produit générant le plus de revenu.
     */
    @JsonProperty("top_revenue_product")
    private String topRevenueProduct;

    /**
     * Bloc des recommandations classées par thème.
     */
    @JsonProperty("recommendations")
    private RecommendationDetailsDTO recommendations;

    /**
     * Résumé exécutif automatique.
     */
    @JsonProperty("executive_summary")
    private String executiveSummary;

    /**
     * Nombre total d'anomalies détectées.
     */
    @JsonProperty("anomalies_count")
    private Integer anomaliesCount;

    /**
     * Nombre d'anomalies prioritaires.
     */
    @JsonProperty("high_priority_anomalies")
    private Integer highPriorityAnomalies;

    /**
     * Bloc de prévision associé aux recommandations.
     */
    @JsonProperty("forecast")
    private ForecastDTO forecast;

    /**
     * Bloc d'anomalies réutilisé comme contexte d'analyse.
     */
    @JsonProperty("anomalies_report")
    private AnomaliesDTO anomaliesReport;
}