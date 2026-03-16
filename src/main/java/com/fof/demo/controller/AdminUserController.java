package com.fof.demo.controller;

import com.fof.demo.dto.ApiResponse;
import com.fof.demo.dto.PagedResponse;
import com.fof.demo.dto.UpdateRoleDTO;
import com.fof.demo.dto.UserDTO;
import com.fof.demo.enums.Role;
import com.fof.demo.service.AdminUserService;
import com.fof.demo.validation.EnumValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
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
     * Exemple d'appels possibles :
     *
     *  GET /api/admin/users
     *  GET /api/admin/users?role=ADMIN
     *  GET /api/admin/users?search=john
     *  GET /api/admin/users?role=USER&search=smith
     *
     * Pagination :
     *
     *  GET /api/admin/users?page=0&size=10
     *
     * Tri :
     *
     *  GET /api/admin/users?sort=firstName,asc
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
    public ApiResponse<PagedResponse<UserDTO>> getUsers(

            @RequestParam(required = false)
            @EnumValidator(enumClass = Role.class, message = "Role must be ADMIN or USER")
            String role,

            @RequestParam(required = false)
            String search,

            @PageableDefault(size = 10)
            Pageable pageable
    ){

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
     * Exemple :
     *
     *      DELETE /api/admin/users/5
     *
     * @param id identifiant de l'utilisateur à supprimer
     *
     * @return ApiResponse confirmant la suppression
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id){

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
     * Exemple :
     *
     *      PUT /api/admin/users/3/role
     *
     * Body :
     *
     * {
     *     "role": "ADMIN"
     * }
     *
     * @param id identifiant de l'utilisateur
     * @param dto DTO contenant le nouveau rôle
     *
     * @return ApiResponse contenant l'utilisateur mis à jour
     */
    @PutMapping("/{id}/role")
    public ApiResponse<UserDTO> updateUserRole(
            @PathVariable Long id,
            @RequestBody UpdateRoleDTO dto
    ){

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