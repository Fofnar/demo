package com.fof.demo.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO représentant la requête envoyée au microservice IA.
 *
 * <p>
 * Contient la liste des ventes à analyser, transformées en {@link AISaleDTO}.
 * Ce format correspond au payload attendu par le service FastAPI.
 * </p>
 *
 * <p>
 * Utilisé dans le pipeline :
 * backend → transformation → envoi vers le moteur IA.
 * </p>
 *
 * @author Fodeba Fofana
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AISalesRequest {

    @NotEmpty
    private List<AISaleDTO> data;
}