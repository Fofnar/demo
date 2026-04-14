package com.fof.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO global représentant la réponse complète du microservice IA.
 *
 * Chaque bloc correspond à une brique fonctionnelle du moteur d'analyse :
 * - ventes
 * - anomalies
 * - prédiction
 * - recommandations
 * - santé business
 * - inventaire
 * - stock
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AIResponse {

    @JsonProperty("sales_analysis")
    private SalesAnalysisDTO salesAnalysis;

    @JsonProperty("anomalies")
    private AnomaliesDTO anomalies;

    @JsonProperty("prediction")
    private PredictionDTO prediction;

    @JsonProperty("recommendations")
    private RecommendationsDTO recommendations;

    @JsonProperty("health_score")
    private BusinessHealthDTO healthScore;

    @JsonProperty("inventory")
    private InventoryDTO inventory;

    @JsonProperty("stock_prediction")
    private StockPredictionDTO stockPrediction;

    @JsonProperty("stock_recommendations")
    private StockRecommendationsDTO stockRecommendations;
}