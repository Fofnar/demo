package com.fof.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO interne contenant les recommandations détaillées.
 *
 * Ce bloc sépare les recommandations par niveau d'analyse :
 * - produit
 * - tendance
 * - anomalies
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecommendationDetailsDTO {

    /**
     * Recommandations liées aux produits les plus importants.
     */
    @JsonProperty("product_insights")
    private List<String> productInsights;

    /**
     * Recommandations liées à la tendance globale.
     */
    @JsonProperty("trend_insights")
    private List<String> trendInsights;

    /**
     * Recommandations liées aux anomalies détectées.
     */
    @JsonProperty("anomaly_insights")
    private List<String> anomalyInsights;
}