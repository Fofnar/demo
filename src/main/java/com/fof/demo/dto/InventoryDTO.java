package com.fof.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO principal pour le bloc "inventory".
 *
 * Ce bloc décrit l'état global du stock :
 * - nombre total de produits analysés
 * - répartition des niveaux de stock
 * - résumé métier
 * - alertes produit par produit
 * - recommandations globales
 *
 *  @author Fodeba Fofana
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class InventoryDTO {

    /**
     * Nombre total de produits suivis.
     */
    @JsonProperty("total_products")
    private Integer totalProducts;

    /**
     * Nombre de produits en stock faible.
     */
    @JsonProperty("low_stock_count")
    private Integer lowStockCount;

    /**
     * Nombre de produits en stock critique.
     */
    @JsonProperty("critical_stock_count")
    private Integer criticalStockCount;

    /**
     * Nombre de produits en rupture de stock.
     */
    @JsonProperty("out_of_stock_count")
    private Integer outOfStockCount;

    /**
     * Résumé métier sur l'état du stock.
     */
    @JsonProperty("inventory_comment")
    private String inventoryComment;

    /**
     * Commentaire global sur la santé du stock.
     */
    @JsonProperty("business_comment")
    private String businessComment;

    /**
     * Liste des alertes stock par produit.
     */
    @JsonProperty("low_stock_alerts")
    private List<InventoryAlertDTO> lowStockAlerts;

    /**
     * Recommandations globales sur l'inventaire.
     */
    @JsonProperty("recommendations")
    private List<String> recommendations;

    /**
     * DTO interne représentant une alerte de stock pour un produit.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InventoryAlertDTO {

        /**
         * Nom du produit concerné.
         */
        @JsonProperty("product")
        private String product;

        /**
         * Quantité de stock restante.
         */
        @JsonProperty("stock")
        private Integer stock;

        /**
         * Niveau de stock : out_of_stock, critical, low, medium, healthy.
         */
        @JsonProperty("stock_level")
        private String stockLevel;

        /**
         * Indication rapide pour l'affichage frontend.
         */
        @JsonProperty("warning")
        private String warning;

        /**
         * Recommandation métier associée au produit.
         */
        @JsonProperty("recommendation")
        private String recommendation;

        /**
         * Indique si le produit est lié à une anomalie amont.
         */
        @JsonProperty("has_related_anomaly")
        private Boolean hasRelatedAnomaly;

        /**
         * Nombre d'anomalies liées à ce produit.
         */
        @JsonProperty("related_anomaly_count")
        private Integer relatedAnomalyCount;

        /**
         * Sévérité maximale observée pour les anomalies liées.
         */
        @JsonProperty("related_anomaly_severity")
        private String relatedAnomalySeverity;
    }
}