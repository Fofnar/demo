package com.fof.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIResponse {

    // Résultat de l'analyse des ventes
    private Map<String, Object> sales_analysis;

    // Détection d'anomalies dans les ventes
    private Map<String, Object> anomalies;

    // Prédiction des revenus
    private Map<String, Object> prediction;

    // Recommandations business
    private Map<String, Object> recommendations;

    // Score global de santé du business
    private Map<String, Object> health_score;

    // Analyse de l'inventaire
    private Map<String, Object> inventory;

    // Prédiction de rupture de stock
    private Map<String, Object> stock_prediction;

    // Recommandations de réapprovisionnement
    private Map<String, Object> stock_recommendations;

}
