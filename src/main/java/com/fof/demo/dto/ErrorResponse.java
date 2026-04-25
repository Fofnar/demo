package com.fof.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO standard représentant une réponse d'erreur de l'API.
 *
 * <p>
 * Utilisé par le GlobalExceptionHandler pour centraliser et uniformiser
 * les réponses d'erreur envoyées au frontend.
 * </p>
 *
 * <p>
 * Permet de fournir une structure cohérente contenant :
 * <ul>
 *     <li>Un indicateur de succès (toujours false en cas d'erreur)</li>
 *     <li>Un message d'erreur explicite</li>
 *     <li>Un timestamp de l'erreur</li>
 * </ul>
 * </p>
 *
 * <p>
 * Ce format facilite le traitement des erreurs côté frontend Angular
 * et améliore la lisibilité des réponses API.
 * </p>
 *
 * @author Fodeba Fofana
 */
@Data
@AllArgsConstructor
public class ErrorResponse {

    private boolean success;
    private String error;
    private LocalDateTime timestamp;
}