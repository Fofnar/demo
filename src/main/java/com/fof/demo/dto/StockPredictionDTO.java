package com.fof.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO principal pour le bloc "stock_prediction".
 *
 * Ce bloc expose :
 * - la couverture cible
 * - la liste des produits analysés
 * - le commentaire métier global
 * - les recommandations globales
 * - le contexte d'anomalies associé
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockPredictionDTO {

    /**
     * Nombre de jours de couverture visé pour le stock.
     */
    @JsonProperty("coverage_target_days")
    private Integer coverageTargetDays;

    /**
     * Liste des produits avec leur risque de rupture estimé.
     */
    @JsonProperty("stock_prediction")
    private List<StockPredictionItemDTO> stockPrediction;

    /**
     * Commentaire métier global sur l'état du stock.
     */
    @JsonProperty("business_comment")
    private String businessComment;

    /**
     * Recommandations globales liées à la prévision de stock.
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
     * DTO représentant la prévision de stock pour un produit.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StockPredictionItemDTO {

        /**
         * Nom du produit analysé.
         */
        @JsonProperty("product")
        private String product;

        /**
         * Stock actuel observé.
         */
        @JsonProperty("current_stock")
        private Integer currentStock;

        /**
         * Moyenne globale des ventes journalières.
         */
        @JsonProperty("avg_daily_sales")
        private Double avgDailySales;

        /**
         * Moyenne récente des ventes journalières.
         */
        @JsonProperty("recent_avg_daily_sales")
        private Double recentAvgDailySales;

        /**
         * Tendance de la demande : upward, downward ou stable.
         */
        @JsonProperty("sales_trend")
        private String salesTrend;

        /**
         * Nombre estimé de jours avant rupture.
         */
        @JsonProperty("estimated_days_before_stockout")
        private Double estimatedDaysBeforeStockout;

        /**
         * Niveau de risque stock.
         */
        @JsonProperty("risk_level")
        private String riskLevel;

        /**
         * Quantité de réapprovisionnement recommandée.
         */
        @JsonProperty("recommended_restock_quantity")
        private Integer recommendedRestockQuantity;

        /**
         * Message métier associé au produit.
         */
        @JsonProperty("recommendation")
        private String recommendation;

        /**
         * Indique si le produit est lié à une anomalie détectée en amont.
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