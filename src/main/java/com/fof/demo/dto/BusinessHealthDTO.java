package com.fof.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO principal pour le bloc "health_score".
 *
 * Ce bloc représente la santé globale du business à partir des ventes :
 * - score global
 * - statut lisible
 * - détail des sous-scores
 * - commentaires métier
 * - recommandations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BusinessHealthDTO {

    /**
     * Score global de santé business sur 100.
     */
    @JsonProperty("health_score")
    private Integer healthScore;

    /**
     * Niveau lisible du score : excellent, good, average, weak, critical.
     */
    @JsonProperty("health_status")
    private String healthStatus;

    /**
     * Détail des sous-scores utilisés pour construire le score global.
     */
    @JsonProperty("health_breakdown")
    private HealthBreakdownDTO healthBreakdown;

    /**
     * Détails textuels expliquant chaque sous-score.
     */
    @JsonProperty("health_details")
    private HealthDetailsDTO healthDetails;

    /**
     * Commentaire métier principal généré automatiquement.
     */
    @JsonProperty("business_comment")
    private String businessComment;

    /**
     * Liste des recommandations business.
     */
    @JsonProperty("recommendations")
    private List<String> recommendations;

    /**
     * Nombre total d'anomalies prises en compte dans le score.
     */
    @JsonProperty("anomalies_count")
    private Integer anomaliesCount;

    /**
     * Nombre d'anomalies prioritaires.
     */
    @JsonProperty("high_priority_anomalies")
    private Integer highPriorityAnomalies;

    /**
     * Sous-bloc décrivant les composantes du score de santé.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HealthBreakdownDTO {

        /**
         * Score lié à la tendance des ventes.
         */
        @JsonProperty("trend_score")
        private Integer trendScore;

        /**
         * Score lié à la stabilité des revenus.
         */
        @JsonProperty("stability_score")
        private Integer stabilityScore;

        /**
         * Score lié à la croissance.
         */
        @JsonProperty("growth_score")
        private Integer growthScore;

        /**
         * Score lié à la diversification produit.
         */
        @JsonProperty("diversification_score")
        private Integer diversificationScore;

        /**
         * Pénalité retirée à cause des anomalies.
         */
        @JsonProperty("anomaly_penalty")
        private Integer anomalyPenalty;
    }

    /**
     * Sous-bloc contenant les explications textuelles des scores.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HealthDetailsDTO {

        /**
         * Commentaire sur la tendance.
         */
        @JsonProperty("trend_comment")
        private String trendComment;

        /**
         * Commentaire sur la stabilité.
         */
        @JsonProperty("stability_comment")
        private String stabilityComment;

        /**
         * Commentaire sur la croissance.
         */
        @JsonProperty("growth_comment")
        private String growthComment;

        /**
         * Commentaire sur la diversification.
         */
        @JsonProperty("diversification_comment")
        private String diversificationComment;

        /**
         * Commentaire sur les anomalies.
         */
        @JsonProperty("anomaly_comment")
        private String anomalyComment;
    }
}