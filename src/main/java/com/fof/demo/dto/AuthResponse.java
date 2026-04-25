package com.fof.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO retourné après une authentification réussie.
 *
 * Contient :
 * - accessToken (token court pour accéder à l'API)
 * - refreshToken (token long pour générer un nouveau access token)
 *
 * @author Fodeba Fofana
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    // Token utilisé pour accéder aux endpoints sécurisés
    private String accessToken;

    // Token utilisé pour générer un nouveau access token
    private String refreshToken;
}