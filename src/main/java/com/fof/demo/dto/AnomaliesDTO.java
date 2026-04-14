package com.fof.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO racine du bloc "anomalies" renvoyé par le microservice IA.
 *
 * Ce bloc contient :
 * - les bornes statistiques
 * - la liste des anomalies détectées
 * - l'information indiquant si le ML est actif
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore les champs non encore modélisés
public class AnomaliesDTO {

    /**
     * Borne basse calculée pour la détection statistique.
     */
    @JsonProperty("lower_bound")
    private Double lowerBound;

    /**
     * Borne haute calculée pour la détection statistique.
     */
    @JsonProperty("upper_bound")
    private Double upperBound;

    /**
     * Liste détaillée des anomalies détectées.
     */
    @JsonProperty("anomalies")
    private List<AnomalyDTO> anomalies;

    /**
     * Indique si la partie ML est activée sur le microservice.
     */
    @JsonProperty("ml_enabled")
    private Boolean mlEnabled;
}