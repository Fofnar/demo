package com.fof.demo.dto;

import com.fof.demo.entity.SaleEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO représentant la réponse d'une vente.
 *
 * <p>
 * Utilisé pour exposer les données de vente au frontend Angular
 * sans exposer directement l'entité {@link SaleEntity}.
 * </p>
 *
 * <p>
 * Inclut une méthode utilitaire permettant de convertir une entité
 * en DTO de manière simple et centralisée.
 * </p>
 *
 * @author Fodeba Fofana
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaleResponseDTO {

    private Long id;
    private String product;
    private int quantity;
    private Double price;
    private LocalDateTime saleDate;
    private int stock;

    /**
     * Convertit une entité {@link SaleEntity} en DTO.
     *
     * @param sale entité de vente
     * @return DTO correspondant
     */
    public static SaleResponseDTO fromEntity(SaleEntity sale) {
        return new SaleResponseDTO(
                sale.getId(),
                sale.getProduct(),
                sale.getQuantity(),
                sale.getPrice(),
                sale.getSaleDate(),
                sale.getStock()
        );
    }
}