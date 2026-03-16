package com.fof.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Lombok génère automatiquement :
// getters
// setters
// toString
// equals
// hashCode
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AISaleDTO {

    // Date de la vente
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;

    // Nom du produit
    @NotBlank
    private String product;

    // Prix du produit
    @NotNull
    @Positive
    private double price;

    // Quantité vendue
    @NotNull
    @Positive
    private int quantity;

    // Stock restant du produit
    @NotNull
    @Positive
    private int stock;

}
