package com.fof.demo.entity;

import com.fof.demo.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;
import jakarta.persistence.*;

/**
 * Entité représentant un utilisateur de l'application.
 *
 * <p>
 * Cette classe est mappée à la table <b>app_user</b> en base de données.
 * Elle contient les informations essentielles liées à un utilisateur,
 * ainsi que son rôle dans le système (USER ou ADMIN).
 * </p>
 *
 * <p>
 * Contraintes appliquées :
 * <ul>
 *     <li>Email unique et valide</li>
 *     <li>Mot de passe d'au moins 8 caractères</li>
 *     <li>Numéro de téléphone unique</li>
 *     <li>Âge supérieur ou égal à 0</li>
 * </ul>
 * </p>
 *
 * <p>
 * Le mot de passe est stocké sous forme encodée (ex : BCrypt)
 * et ne doit jamais être manipulé en clair après l'inscription.
 * </p>
 *
 * @author Fodeba Fofana
 */
@Entity
@Table(name = "app_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUser {

    /**
     * Identifiant unique de l'utilisateur (clé primaire).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Adresse email de l'utilisateur (unique).
     */
    @Column(unique = true, nullable = false)
    @Email
    private String email;

    /**
     * Nom de famille de l'utilisateur.
     */
    @Column(nullable = false)
    private String lastName;

    /**
     * Prénom de l'utilisateur.
     */
    @Column(nullable = false)
    private String firstName;

    /**
     * Âge de l'utilisateur (doit être positif ou nul).
     */
    @Min(0)
    private int age;

    /**
     * Mot de passe encodé de l'utilisateur.
     *
     * <p>
     * Doit contenir au minimum 8 caractères avant encodage.
     * </p>
     */
    @Column(nullable = false)
    @Size(min = 8)
    private String password;

    /**
     * Numéro de téléphone de l'utilisateur (unique).
     */
    @Column(unique = true, nullable = false)
    private String phone;

    /**
     * Rôle de l'utilisateur dans l'application.
     *
     * <p>
     * Stocké sous forme de chaîne en base (STRING) pour plus de lisibilité.
     * </p>
     */
    @Enumerated(EnumType.STRING)
    private Role role;
}