package com.fof.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO principal pour le bloc de prédiction des revenus.
 *
 * Ce bloc contient :
 * - la prévision du lendemain
 * - une prévision moyenne sur 3 jours
 * - la tendance court terme
 * - une qualité de modèle
 * - un commentaire business
 * - le détail des anomalies associées
 * - une projection sur 3 jours
 *
 * @author Fodeba Fofana
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PredictionDTO {

    /**
     * Revenu prédit pour le prochain jour.
     */
    @JsonProperty("predicted_next_day_revenue")
    private Double predictedNextDayRevenue;

    /**
     * Tendance du prochain jour.
     */
    @JsonProperty("trend_next")
    private String trendNext;

    /**
     * Moyenne des prévisions sur les 3 prochains jours.
     */
    @JsonProperty("next_3_days_prediction")
    private Double next3DaysPrediction;

    /**
     * Tendance estimée sur les 3 prochains jours.
     */
    @JsonProperty("trend_next_3_days")
    private String trendNext3Days;

    /**
     * Méthode utilisée pour produire la prévision principale.
     */
    @JsonProperty("prediction_method")
    private String predictionMethod;

    /**
     * Qualité du modèle mesurée par MAE.
     */
    @JsonProperty("model_quality_mae")
    private Double modelQualityMae;

    /**
     * Commentaire métier généré à partir des prédictions et des anomalies.
     */
    @JsonProperty("business_comment")
    private String businessComment;

    /**
     * Nombre total d'anomalies liées au contexte de prévision.
     */
    @JsonProperty("anomalies_count")
    private Integer anomaliesCount;

    /**
     * Nombre d'anomalies considérées comme prioritaires.
     */
    @JsonProperty("high_priority_anomalies")
    private Integer highPriorityAnomalies;

    /**
     * Bloc d'anomalies utilisé comme contexte de la prévision.
     */
    @JsonProperty("anomalies_report")
    private AnomaliesDTO anomaliesReport;

    /**
     * Prévision détaillée sur 3 jours.
     */
    @JsonProperty("forecast_next_3_days")
    private List<ForecastDayDTO> forecastNext3Days;
}