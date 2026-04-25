package com.fof.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entité représentant une transaction de vente.
 *
 * <p>
 * Cette classe est mappée à la table <b>sales</b> en base de données.
 * Elle constitue la source principale de données pour :
 * <ul>
 *     <li>Les analyses métier (KPI, tableaux de bord)</li>
 *     <li>La détection d'anomalies</li>
 *     <li>Les prédictions de ventes</li>
 *     <li>Les recommandations</li>
 * </ul>
 * </p>
 *
 * <p>
 * Chaque enregistrement représente une vente d’un produit à un instant donné,
 * avec des informations sur la quantité, le prix et le niveau de stock restant.
 * </p>
 *
 * <p>
 * Cette entité est utilisée dans un pipeline data complet :
 * base de données → backend → microservice IA → dashboard.
 * </p>
 *
 * @author Fodeba Fofana
 */
@Entity
@Table(name = "sales")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SaleEntity {

    /**
     * Identifiant unique de la vente (clé primaire).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nom du produit vendu.
     */
    @NotBlank
    private String product;

    /**
     * Quantité vendue.
     */
    @Positive
    private int quantity;

    /**
     * Prix unitaire du produit.
     */
    @NotNull
    @Positive
    private Double price;

    /**
     * Date et heure de la vente.
     *
     * <p>
     * Formatée pour l'échange JSON avec le frontend et le microservice IA.
     * </p>
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime saleDate;

    /**
     * Niveau de stock restant après la vente.
     */
    @NotNull
    @Min(0)
    private int stock;

    /**
     * Utilisateur associé à la vente.
     *
     * <p>
     * Relation Many-to-One :
     * plusieurs ventes peuvent être associées à un même utilisateur.
     * </p>
     *
     * <p>
     * Chargement en mode LAZY pour optimiser les performances.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser user;
}