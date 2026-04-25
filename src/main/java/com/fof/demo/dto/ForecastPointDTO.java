package com.fof.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO représentant un point de prévision.
 *
 * Utilisé dans le bloc "forecast" pour exposer la prévision jour par jour.
 *
 *  @author Fodeba Fofana
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForecastPointDTO {

    /**
     * Date de la prévision au format yyyy-MM-dd.
     */
    @JsonProperty("date")
    private String date;

    /**
     * Valeur prévue pour cette date.
     */
    @JsonProperty("yhat")
    private Double yhat;
}