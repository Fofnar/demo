package com.fof.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO représentant les statistiques globales de l'application.
 *
 * <p>
 * Utilisé pour alimenter le dashboard administrateur avec des indicateurs clés :
 * nombre d'utilisateurs, volume de ventes, activité du jour et état du stock.
 * </p>
 *
 * <p>
 * Ces données sont agrégées côté backend et exposées via l'API
 * pour consommation par le frontend Angular.
 * </p>
 *
 * @author Fodeba Fofana
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminStatsDTO {

    private long totalUsers;
    private long totalSales;
    private long salesToday;
    private long totalProducts;
    private long lowStockProducts;
}