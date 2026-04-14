package com.fof.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO principal pour le bloc "stock_recommendations".
 *
 * Ce bloc synthétise la lecture métier de la prévision de stock :
 * - alertes critiques
 * - alertes élevées
 * - note exécutive
 * - recommandations détaillées
 * - contexte d'anomalies
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockRecommendationsDTO {

    /**
     * Nombre d'alertes critiques.
     */
    @JsonProperty("critical_alerts")
    private Integer criticalAlerts;

    /**
     * Nombre d'alertes élevées.
     */
    @JsonProperty("high_alerts")
    private Integer highAlerts;

    /**
     * Note exécutive synthétique.
     */
    @JsonProperty("executive_note")
    private String executiveNote;

    /**
     * Commentaire métier global sur le stock.
     */
    @JsonProperty("business_comment")
    private String businessComment;

    /**
     * Recommandations globales héritées du bloc de prévision.
     */
    @JsonProperty("recommendations")
    private List<String> recommendations;

    /**
     * Nombre total d'anomalies détectées en amont.
     */
    @JsonProperty("anomalies_count")
    private Integer anomaliesCount;

    /**
     * Nombre d'anomalies jugées prioritaires.
     */
    @JsonProperty("high_priority_anomalies")
    private Integer highPriorityAnomalies;

    /**
     * Bloc d'anomalies utilisé comme contexte d'analyse.
     */
    @JsonProperty("anomalies_report")
    private AnomaliesDTO anomaliesReport;

    /**
     * Liste détaillée des recommandations par produit.
     */
    @JsonProperty("stock_recommendations")
    private List<StockRecommendationItemDTO> stockRecommendations;

    /**
     * DTO représentant une recommandation de stock pour un produit.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StockRecommendationItemDTO {

        /**
         * Nom du produit concerné.
         */
        @JsonProperty("product")
        private String product;

        /**
         * Niveau de risque associé.
         */
        @JsonProperty("risk_level")
        private String riskLevel;

        /**
         * Message métier principal.
         */
        @JsonProperty("message")
        private String message;

        /**
         * Quantité recommandée pour le réapprovisionnement.
         */
        @JsonProperty("recommended_restock_quantity")
        private Integer recommendedRestockQuantity;

        /**
         * Estimation du nombre de jours avant rupture.
         */
        @JsonProperty("estimated_days_before_stockout")
        private Double estimatedDaysBeforeStockout;

        /**
         * Indique si une anomalie amont est liée au produit.
         */
        @JsonProperty("has_related_anomaly")
        private Boolean hasRelatedAnomaly;

        /**
         * Nombre d'anomalies liées à ce produit.
         */
        @JsonProperty("related_anomaly_count")
        private Integer relatedAnomalyCount;

        /**
         * Sévérité maximale des anomalies liées.
         */
        @JsonProperty("related_anomaly_severity")
        private String relatedAnomalySeverity;
    }
}