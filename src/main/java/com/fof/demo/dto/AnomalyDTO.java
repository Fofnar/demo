package com.fof.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO représentant une anomalie de vente détectée par le microservice IA.
 *
 * Ce bloc contient le détail métier d'une anomalie :
 * - date concernée
 * - produit impacté
 * - valeur observée
 * - gravité
 * - explication métier
 * - recommandation
 * - sources de détection
 * - score ML éventuel
 *
 * @author Fodeba Fofana
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // Sécurité si le format évolue côté IA
public class AnomalyDTO {

    /**
     * Date de l'anomalie.
     */
    @JsonProperty("date")
    private String date;

    /**
     * Produit concerné par l'anomalie.
     */
    @JsonProperty("product")
    private String product;

    /**
     * Valeur du produit sur le jour concerné.
     */
    @JsonProperty("value")
    private Double value;

    /**
     * Chiffre d'affaires total du jour.
     */
    @JsonProperty("daily_revenue")
    private Double dailyRevenue;

    /**
     * Borne basse statistique utilisée pour l'analyse.
     */
    @JsonProperty("lower_bound")
    private Double lowerBound;

    /**
     * Borne haute statistique utilisée pour l'analyse.
     */
    @JsonProperty("upper_bound")
    private Double upperBound;

    /**
     * Valeur de référence utilisée pour qualifier l'écart.
     */
    @JsonProperty("reference_value")
    private Double referenceValue;

    /**
     * Niveau de gravité de l'anomalie.
     */
    @JsonProperty("severity")
    private String severity;

    /**
     * Explication métier de l'anomalie.
     */
    @JsonProperty("reason")
    private String reason;

    /**
     * Action recommandée.
     */
    @JsonProperty("recommendation")
    private String recommendation;

    /**
     * Part du produit dans le chiffre d'affaires du jour.
     */
    @JsonProperty("share_of_day_revenue")
    private Double shareOfDayRevenue;

    /**
     * Intensité de l'écart détecté.
     */
    @JsonProperty("deviation_ratio")
    private Double deviationRatio;

    /**
     * Origines de la détection : statistique, ML, ou les deux.
     */
    @JsonProperty("sources")
    private List<String> sources;

    /**
     * Score fourni par le modèle ML quand il est disponible.
     */
    @JsonProperty("ml_score")
    private Double mlScore;
}