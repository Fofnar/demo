package com.fof.demo.controller;

import com.fof.demo.dto.ApiResponse;
import com.fof.demo.dto.UpdateUserDTO;
import com.fof.demo.dto.UserDTO;
import com.fof.demo.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Contrôleur responsable de la gestion du profil utilisateur.
 *
 * Les endpoints permettent à l'utilisateur authentifié :
 * - de consulter ses informations
 * - de modifier son profil
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
     *  Récupérer les informations de l'utilisateur connecté.
     *
     * URL : GET /api/users/me
     *
     * L'objet Authentication contient les informations de l'utilisateur
     * authentifié par Spring Security (notamment son email).
     */
    @GetMapping("/me")
    public ApiResponse<UserDTO> getCurrentUser(Authentication authentication){

        // Email récupéré depuis le token JWT
        String email = authentication.getName();

        // Récupération de l'utilisateur
        UserDTO user = userService.getCurrentUser(email);

        // Réponse API standardisée
        return new ApiResponse<>(
                true,
                user,
                "User fetched successfully",
                LocalDateTime.now()
        );
    }

    /**
     *  Mise à jour du profil utilisateur.
     *
     * URL : PUT /api/users/me
     *
     * L'utilisateur peut modifier :
     * - email
     * - prénom
     * - nom
     * - âge
     * - téléphone
     *
     * Le rôle ne peut pas être modifié ici
     * (cela doit être fait par un administrateur).
     */
    @PutMapping("/me")
    public ApiResponse<UserDTO> updateCurrentUser(
            Authentication authentication,
            @RequestBody UpdateUserDTO updateUserDTO
    ){

        // Email récupéré depuis le token JWT
        String email = authentication.getName();

        // Mise à jour du profil utilisateur
        UserDTO user = userService.updateUser(email, updateUserDTO);

        // Réponse API standardisée
        return new ApiResponse<>(
                true,
                user,
                "Update completed successfully",
                LocalDateTime.now()
        );
    }

}