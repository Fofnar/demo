package com.fof.demo.dto;

import com.fof.demo.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO utilisé pour la mise à jour du rôle d'un utilisateur.
 *
 * <p>
 * Permet de modifier le rôle (USER, ADMIN, etc.) via une requête API,
 * généralement dans un contexte administratif.
 * </p>
 *
 * @author Fodeba Fofana
 */
@Data
public class UpdateRoleDTO {

    @NotNull
    private Role role;
}