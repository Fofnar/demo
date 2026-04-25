package com.fof.demo.service;

import com.fof.demo.dto.PagedResponse;
import com.fof.demo.dto.UserDTO;
import com.fof.demo.entity.AppUser;
import com.fof.demo.enums.Role;
import com.fof.demo.repository.AppUserRepository;
import com.fof.demo.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.fof.demo.specification.UserSpecification.search;

/**
 * Service métier dédié aux opérations administrateur
 * concernant la gestion des utilisateurs.
 *
 * Ce service permet notamment :
 * - de récupérer la liste des utilisateurs avec pagination
 * - de filtrer les utilisateurs par rôle
 * - de supprimer un utilisateur
 * - de modifier le rôle d'un utilisateur
 *
 * La méthode getAllUsers utilise une recherche dynamique (search) et un filtrage par rôle.
 *
 * Les méthodes de ce service sont généralement appelées
 * depuis un contrôleur réservé aux administrateurs.
 *
 * @author Fodeba Fofana
 */
@Service
@RequiredArgsConstructor
public class AdminUserService {

    /**
     * Repository permettant l'accès aux données
     * de l'entité AppUser dans la base de données.
     */
    private final AppUserRepository userRepository;

    /**
     * Récupère la liste des utilisateurs avec pagination.
     *
     * Possibilité de filtrer les résultats par rôle.
     *
     * @param role rôle de l'utilisateur (optionnel)
     * @param pageable informations de pagination
     *                 (numéro de page, taille, tri)
     *
     * @return Page contenant les utilisateurs convertis en UserDTO
     */
    public PagedResponse<UserDTO> getAllUsers(String role, String search, Pageable pageable){

        /** Conversion du rôle String en Enum (null si aucun filtre) */
        Role roleEnum = role !=null ? Role.valueOf(role) : null;

        /**
         * Création d'une Specification dynamique
         *   - hasRole(roleEnum) filtre par rôle si roleEnum != null
         *   - search(search) permet la recherche dynamique par nom, prénom, email ou telephone
         */
        Specification<AppUser> spec =
                Specification.where(UserSpecification.hasRole(roleEnum))
                        .and(search(search));

        /** Récupération des utilisateurs via la Specification avec pagination & tri */
        Page<AppUser> users = userRepository.findAll(spec, pageable);


        /**
         * Conversion des entités AppUser en DTO.
         *
         * Cela permet de ne pas exposer directement l'entité
         * de la base de données au client.
         */
        List<UserDTO> userDTOList = users.getContent()
                .stream()
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getEmail(),
                        user.getLastName(),
                        user.getFirstName(),
                        user.getAge(),
                        user.getPhone(),
                        user.getRole()
                ))
                .toList();

        return new PagedResponse<>(
                userDTOList,
                users.getNumber(),
                users.getSize(),
                users.getTotalElements(),
                users.getTotalPages()
        );
    }

    /**
     * Supprime un utilisateur à partir de son identifiant.
     *
     * @param id identifiant de l'utilisateur à supprimer
     */
    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }

    /**
     * Met à jour le rôle d'un utilisateur.
     *
     * @param id identifiant de l'utilisateur
     * @param role nouveau rôle à attribuer
     *
     * @return UserDTO représentant l'utilisateur mis à jour
     */
    public UserDTO updateUserRole(Long id, Role role){

        /**
         * Recherche de l'utilisateur en base de données.
         * Si l'utilisateur n'existe pas, une exception est levée.
         */
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        /**
         * Mise à jour du rôle de l'utilisateur.
         */
        user.setRole(role);

        /**
         * Sauvegarde des modifications dans la base.
         */
        AppUser saved = userRepository.save(user);

        /**
         * Conversion de l'entité sauvegardée en DTO
         * avant de la retourner au contrôleur.
         */
        return new UserDTO(
                saved.getId(),
                saved.getEmail(),
                saved.getLastName(),
                saved.getFirstName(),
                saved.getAge(),
                saved.getPhone(),
                saved.getRole()
        );
    }
}