package com.fof.demo.dto;

import lombok.Data;

/**
 * DTO utilisé pour envoyer un refresh token
 * afin d'obtenir un nouveau access token.
 *
 * @author Fodeba Fofana
 */
@Data
public class RefreshRequest {

    private String refreshToken;

}