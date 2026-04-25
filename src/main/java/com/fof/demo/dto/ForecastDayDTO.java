package com.fof.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO représentant une ligne de prévision journalière.
 *
 * Utilisé dans le bloc "forecast_next_3_days" pour exposer
 * les valeurs prédites jour par jour au frontend ou au backend.
 *
 *  @author Fodeba Fofana
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForecastDayDTO {

    /**
     * Date de la prévision au format yyyy-MM-dd.
     */
    @JsonProperty("date")
    private String date;

    /**
     * Revenu prédit pour ce jour.
     */
    @JsonProperty("predicted_revenue")
    private Double predictedRevenue;

    /**
     * Méthode utilisée pour produire la prévision.
     */
    @JsonProperty("method")
    private String method;
}