package com.fof.demo.controller;

import com.fof.demo.dto.ApiResponse;
import com.fof.demo.dto.UpdateUserDTO;
import com.fof.demo.dto.UserDTO;
import com.fof.demo.service.AppUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Contrôleur responsable de la gestion du profil utilisateur.
 *
 * Les endpoints permettent à l'utilisateur authentifié :
 * - de consulter ses informations
 * - de modifier son profil
 *
 * @author Fodeba Fofana
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {

    /**
     * Service métier responsable de la gestion des utilisateurs.
     */
    private final AppUserService userService;

    /**
     * Récupérer les informations de l'utilisateur connecté.
     *
     * URL : GET /api/users/me
     *
     * L'objet Authentication contient les informations de l'utilisateur
     * authentifié par Spring Security (notamment son email).
     *
     * @param authentication objet d'authentification Spring Security
     * @return informations de l'utilisateur connecté
     */
    @GetMapping("/me")
    @Operation(
            summary = "Get current user profile",
            description = "Retrieves the profile of the authenticated user."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User fetched successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser(Authentication authentication) {

        // Email récupéré depuis le token JWT
        String email = authentication.getName();

        // Récupération de l'utilisateur
        UserDTO user = userService.getCurrentUser(email);

        // Réponse API standardisée
        ApiResponse<UserDTO> response = new ApiResponse<>(
                true,
                user,
                "User fetched successfully",
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Mise à jour du profil utilisateur.
     *
     * URL : PUT /api/users/me
     *
     * L'utilisateur peut modifier :
     * - email
     * - prénom
     * - nom
     * - âge
     * - téléphone
     * - mot de pass
     *
     * Le rôle ne peut pas être modifié ici
     * (cela doit être fait par un administrateur).
     *
     * @param authentication objet d'authentification Spring Security
     * @param updateUserDTO données de mise à jour du profil
     * @return profil utilisateur mis à jour
     */
    @PutMapping("/me")
    @Operation(
            summary = "Update current user profile",
            description = "Updates the profile of the authenticated user."
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
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User profile update payload",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = UpdateUserDTO.class)
            )
    )
    public ResponseEntity<ApiResponse<UserDTO>> updateCurrentUser(
            Authentication authentication,
            @RequestBody @Valid UpdateUserDTO updateUserDTO
    ) {

        // Email récupéré depuis le token JWT
        String email = authentication.getName();

        // Mise à jour du profil utilisateur
        UserDTO user = userService.updateUser(email, updateUserDTO);

        // Réponse API standardisée
        ApiResponse<UserDTO> response = new ApiResponse<>(
                true,
                user,
                "Update completed successfully",
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }
}