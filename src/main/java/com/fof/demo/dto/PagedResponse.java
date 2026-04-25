package com.fof.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * DTO standard représentant une réponse paginée de l'API.
 *
 * <p>
 * Encapsule les données paginées ainsi que les informations de pagination
 * nécessaires au frontend pour naviguer dans les résultats.
 * </p>
 *
 * <p>
 * Utilisé notamment pour :
 * <ul>
 *     <li>Les listes d'utilisateurs</li>
 *     <li>Les ventes paginées</li>
 *     <li>Les résultats filtrés avec pagination</li>
 * </ul>
 * </p>
 *
 * <p>
 * Compatible avec les composants de pagination du frontend Angular.
 * </p>
 *
 * @param <T> type des éléments contenus dans la page
 *
 * @author Fodeba Fofana
 */
@Data
@AllArgsConstructor
public class PagedResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}