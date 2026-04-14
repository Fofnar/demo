package com.fof.demo.dto;

import com.fof.demo.entity.SaleEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data // Génère automatiquement les accesseurs et méthodes utilitaires
@AllArgsConstructor // Génère le constructeur complet
@NoArgsConstructor // Génère le constructeur sans argument

/** DTO de réponse pour une vente */
public class SaleResponseDTO {

    private Long id; // Identifiant technique de la vente
    private String product; // Nom du produit vendu
    private int quantity; // Quantité vendue
    private Double price; // Prix unitaire
    private LocalDateTime saleDate; // Date de la vente
    private int stock; // Stock restant

    public static SaleResponseDTO fromEntity(SaleEntity sale) { // Convertit une entité en DTO
        return new SaleResponseDTO( // Construit le DTO final
                sale.getId(), // Récupère l'identifiant
                sale.getProduct(), // Récupère le produit
                sale.getQuantity(), // Récupère la quantité
                sale.getPrice(), // Récupère le prix
                sale.getSaleDate(), // Récupère la date de vente
                sale.getStock() // Récupère le stock
        );
    }
}