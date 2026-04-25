package com.fof.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO générique standardisant les réponses de l'API REST.
 *
 * <p>
 * Permet d'encapsuler toutes les réponses du backend dans un format uniforme :
 * succès, données retournées, message et timestamp.
 * </p>
 *
 * <p>
 * Objectifs :
 * <ul>
 *     <li>Uniformiser les réponses côté frontend Angular</li>
 *     <li>Faciliter la gestion des succès et erreurs</li>
 *     <li>Améliorer la lisibilité et la maintenabilité de l'API</li>
 * </ul>
 * </p>
 *
 * @param <T> type des données retournées
 *
 * @author Fodeba Fofana
 */
@Data
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private String message;
    private LocalDateTime timestamp;
}