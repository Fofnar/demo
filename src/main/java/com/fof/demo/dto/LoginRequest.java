package com.fof.demo.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO représentant la requête de connexion.
 *
 * <p>
 * Contient les identifiants nécessaires à l'authentification
 * d'un utilisateur dans le système.
 * </p>
 *
 * <p>
 * Les validations garantissent que les données envoyées sont valides
 * avant traitement par le backend.
 * </p>
 *
 * @author Fodeba Fofana
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @NotBlank
    @Size(min = 8)
    @NotBlank(message = "Password is required")
    private String password;
}