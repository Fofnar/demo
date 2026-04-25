package com.fof.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO représentant le chiffre d'affaires agrégé par jour.
 *
 * Utilisé dans le bloc "sales_analysis" pour afficher l'évolution
 * du revenu dans le temps (ex: graphique frontend).
 *
 * Exemple JSON :
 * {
 *   "date": "2025-01-01",
 *   "revenue": 3700
 * }
 *
 * @author Fodeba Fofana
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore les champs inconnus venant du microservice IA
public class RevenuePerDayDTO {

    /**
     * Date du jour (format ISO : yyyy-MM-dd)
     */
    @JsonProperty("date")
    private String date;

    /**
     * Revenu total généré pour ce jour
     */
    @JsonProperty("revenue")
    private Double revenue;
}