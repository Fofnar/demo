package com.fof.demo.repository;

import com.fof.demo.entity.AppUser;
import com.fof.demo.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * Repository d'accès aux données des utilisateurs.
 *
 * <p>
 * Cette interface permet d'effectuer les opérations CRUD sur l'entité {@link AppUser}
 * ainsi que des requêtes spécifiques liées à l'authentification, au filtrage
 * et à la gestion des utilisateurs.
 * </p>
 *
 * <p>
 * Fonctionnalités principales :
 * <ul>
 *     <li>Recherche d'un utilisateur par email (authentification)</li>
 *     <li>Recherche par numéro de téléphone</li>
 *     <li>Filtrage des utilisateurs par rôle avec pagination</li>
 *     <li>Support des requêtes dynamiques via {@link JpaSpecificationExecutor}</li>
 * </ul>
 * </p>
 *
 * <p>
 * Ce repository est utilisé par les services métier pour alimenter
 * le système d'authentification et les fonctionnalités administratives.
 * </p>
 *
 * @author Fodeba Fofana
 */
public interface AppUserRepository extends
        JpaRepository<AppUser, Long>,
        JpaSpecificationExecutor<AppUser> {

    /**
     * Récupère un utilisateur à partir de son email.
     *
     * <p>
     * L'email est utilisé comme identifiant principal pour l'authentification.
     * </p>
     *
     * @param email email de l'utilisateur
     * @return un {@link Optional} contenant l'utilisateur si trouvé
     */
    Optional<AppUser> findByEmail(String email);

    /**
     * Récupère les utilisateurs filtrés par rôle avec pagination.
     *
     * <p>
     * Utilisé notamment dans les interfaces d'administration
     * pour gérer les utilisateurs.
     * </p>
     *
     * @param role rôle des utilisateurs à filtrer
     * @param pageable informations de pagination
     * @return une page d'utilisateurs correspondant au rôle
     */
    Page<AppUser> findByRole(Role role, Pageable pageable);

    /**
     * Récupère un utilisateur à partir de son numéro de téléphone.
     *
     * @param phone numéro de téléphone
     * @return un {@link Optional} contenant l'utilisateur si trouvé
     */
    Optional<AppUser> findByPhone(String phone);
}