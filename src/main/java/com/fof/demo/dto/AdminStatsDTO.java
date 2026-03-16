package com.fof.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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