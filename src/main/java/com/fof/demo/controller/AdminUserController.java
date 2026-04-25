package com.fof.demo.controller;

import com.fof.demo.dto.ApiResponse;
import com.fof.demo.dto.PagedResponse;
import com.fof.demo.dto.UpdateRoleDTO;
import com.fof.demo.dto.UserDTO;
import com.fof.demo.enums.Role;
import com.fof.demo.service.AdminUserService;
import com.fof.demo.validation.EnumValidator;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * REST Controller dédié aux opérations administrateur
 * pour la gestion des utilisateurs.
 *
 * Ce contrôleur expose plusieurs endpoints permettant :
 * - de récupérer la liste des utilisateurs avec pagination
 * - d'effectuer une recherche par mot-clé
 * - de filtrer les utilisateurs par rôle
 * - de supprimer un utilisateur
 * - de modifier le rôle d'un utilisateur
 *
 * Toutes les routes de ce contrôleur sont préfixées par :
 *
 *      /api/admin/users
 *
 * Ce type de contrôleur est généralement protégé
 * par un système d'authentification (JWT) et
 * accessible uniquement aux administrateurs.
 *
 * @author Fodeba Fofana
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AdminUserController {

    /**
     * Service métier responsable de la logique
     * de gestion des utilisateurs côté administrateur.
     */
    private final AdminUserService adminUserService;

    /**
     * Endpoint permettant de récupérer la liste des utilisateurs.
     *
     * Fonctionnalités supportées :
     * - Pagination
     * - Tri (sorting)
     * - Filtrage par rôle
     * - Recherche textuelle (search)
     *
     * @param role filtre optionnel permettant de récupérer
     *             uniquement les utilisateurs ayant un rôle spécifique
     *
     * @param search mot-clé permettant d'effectuer une recherche
     *               sur plusieurs champs utilisateur
     *               (firstName, lastName, email, phone)
     *
     * @param pageable objet Spring permettant de gérer :
     *                 - pagination
     *                 - tri
     *
     * @return ApiResponse contenant une réponse paginée d'utilisateurs
     */
    @GetMapping
    @Operation(
            summary = "Get users list",
            description = "Retrieves users with optional role filter, keyword search, pagination and sorting."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Users retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameters"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ApiResponse<PagedResponse<UserDTO>> getUsers(

            @Parameter(
                    description = "Optional user role filter",
                    example = "ADMIN"
            )
            @RequestParam(required = false)
            @EnumValidator(enumClass = Role.class, message = "Role must be ADMIN or USER")
            String role,

            @Parameter(
                    description = "Optional keyword used to search users",
                    example = "john"
            )
            @RequestParam(required = false)
            String search,

            @ParameterObject
            @PageableDefault(size = 10)
            Pageable pageable
    ) {
        // Appel du service pour récupérer les utilisateurs selon les filtres fournis
        PagedResponse<UserDTO> userList =
                adminUserService.getAllUsers(role, search, pageable);

        // Construction d'une réponse standardisée pour l'API
        return new ApiResponse<>(
                true,
                userList,
                "Users retrieved successfully",
                LocalDateTime.now()
        );
    }

    /**
     * Endpoint permettant à un administrateur
     * de supprimer un utilisateur à partir de son identifiant.
     *
     * @param id identifiant de l'utilisateur à supprimer
     *
     * @return ApiResponse confirmant la suppression
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete user by id",
            description = "Deletes a user using their unique identifier."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User deleted successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid user id"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ApiResponse<Void> deleteUser(
            @Parameter(
                    description = "User identifier",
                    example = "5",
                    required = true
            )
            @PathVariable Long id
    ) {
        // Suppression de l'utilisateur via le service
        adminUserService.deleteUser(id);

        // Réponse de confirmation (aucune donnée retournée)
        return new ApiResponse<>(
                true,
                null,
                "User deleted successfully",
                LocalDateTime.now()
        );
    }

    /**
     * Endpoint permettant à un administrateur
     * de modifier le rôle d'un utilisateur.
     *
     * @param id identifiant de l'utilisateur
     * @param dto DTO contenant le nouveau rôle
     *
     * @return ApiResponse contenant l'utilisateur mis à jour
     */
    @PutMapping("/{id}/role")
    @Operation(
            summary = "Update user role",
            description = "Updates the role of an existing user."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Update completed successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Role update payload",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = UpdateRoleDTO.class),
                    examples = @ExampleObject(
                            name = "Update role example",
                            value = """
                                    {
                                      "role": "ADMIN"
                                    }
                                    """
                    )
            )
    )
    public ApiResponse<UserDTO> updateUserRole(
            @Parameter(
                    description = "User identifier",
                    example = "3",
                    required = true
            )
            @PathVariable Long id,
            @RequestBody UpdateRoleDTO dto
    ) {
        // Mise à jour du rôle via le service
        UserDTO user = adminUserService.updateUserRole(id, dto.getRole());

        // Réponse contenant l'utilisateur mis à jour
        return new ApiResponse<>(
                true,
                user,
                "Update completed successfully",
                LocalDateTime.now()
        );
    }
}