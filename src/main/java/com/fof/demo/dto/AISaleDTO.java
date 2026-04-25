package com.fof.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO représentant une vente destinée au microservice IA.
 *
 * <p>
 * Utilisé pour transférer les données de ventes depuis le backend
 * vers le service d'analyse (FastAPI) dans un format simplifié et exploitable.
 * </p>
 *
 * <p>
 * Ce DTO constitue une étape clé du pipeline data :
 * transformation des entités métier en données prêtes pour le traitement
 * (prédictions, anomalies, recommandations).
 * </p>
 *
 * @author Fodeba Fofana
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AISaleDTO {

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;

    @NotBlank
    private String product;

    @NotNull
    @Positive
    private double price;

    @NotNull
    @Positive
    private int quantity;

    @NotNull
    @Min(0)
    private int stock;
}