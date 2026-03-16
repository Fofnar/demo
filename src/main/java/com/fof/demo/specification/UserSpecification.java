package com.fof.demo.specification;

import com.fof.demo.entity.AppUser;
import com.fof.demo.enums.Role;
import org.springframework.data.jpa.domain.Specification;

/**
 * Classe utilitaire contenant les Specifications utilisées
 * pour construire dynamiquement des requêtes de recherche
 * sur l'entité AppUser.
 *
 * Les Specifications permettent de créer des filtres dynamiques
 * qui peuvent être combinés entre eux (AND / OR) afin de construire
 * des requêtes complexes de manière flexible.
 *
 * Exemple d'utilisation dans un service :
 *
 * Specification<AppUser> spec =
 *      Specification.where(UserSpecification.hasRole(role))
 *                   .and(UserSpecification.search(keyword));
 *
 * Page<AppUser> users = userRepository.findAll(spec, pageable);
 */
public class UserSpecification {

    /**
     * Filtre permettant de récupérer les utilisateurs
     * ayant un rôle spécifique.
     *
     * Si le rôle est null, aucun filtre n'est appliqué
     * (la specification retourne null).
     *
     * @param role rôle à filtrer
     * @return Specification appliquant le filtre sur le rôle
     */
    public static Specification<AppUser> hasRole(Role role) {

        return (root, query, cb) ->
                role == null ? null : cb.equal(root.get("role"), role);
    }

    /**
     * Filtre de recherche textuelle sur plusieurs champs
     * de l'utilisateur.
     *
     * La recherche est effectuée sur :
     * - le prénom (firstName)
     * - le nom (lastName)
     * - l'email
     * -le numéro de téléphone (phone)
     *
     * La recherche est insensible à la casse grâce
     * à l'utilisation de lower().
     *
     * Exemple :
     * keyword = "john"
     *
     * Correspondra à :
     * - John
     * - john
     * - JOHN
     *
     * @param keyword mot-clé recherché
     * @return Specification appliquant la recherche textuelle
     */
    public static Specification<AppUser> search(String keyword) {

        return (root, query, cb) -> {

            /**
             * Si aucun mot-clé n'est fourni,
             * aucun filtre n'est appliqué.
             */
            if (keyword == null || keyword.trim().isEmpty()) {
                return null;
            }

            /**
             * Construction du pattern LIKE utilisé pour
             * la recherche partielle dans la base de données.
             *
             * Exemple :
             * keyword = "john"
             * pattern = "%john%"
             */
            String likePattern = "%" + keyword.toLowerCase() + "%";

            /**
             * Construction de la condition OR permettant
             * de rechercher dans plusieurs champs.
             *
             * La requête correspondra si l'un des champs
             * contient le mot-clé recherché.
             */
            return cb.or(
                    cb.like(cb.lower(root.get("firstName")), likePattern),
                    cb.like(cb.lower(root.get("lastName")), likePattern),
                    cb.like(cb.lower(root.get("email")), likePattern),
                    cb.like(root.get("phone"), likePattern)
            );
        };
    }
}