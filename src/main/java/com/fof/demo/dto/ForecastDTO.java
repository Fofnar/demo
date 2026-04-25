package com.fof.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO représentant le bloc de prévision associé aux recommandations.
 *
 * Ce bloc contient :
 * - l'état de disponibilité du forecast
 * - la méthode utilisée
 * - la liste des valeurs prédites
 *
 *  @author Fodeba Fofana
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForecastDTO {

    /**
     * Indique si une prévision a pu être produite.
     */
    @JsonProperty("available")
    private Boolean available;

    /**
     * Méthode utilisée : prophet, linear, none, etc.
     */
    @JsonProperty("method")
    private String method;

    /**
     * Liste des points de prévision.
     */
    @JsonProperty("forecast")
    private List<ForecastPointDTO> forecast;
}