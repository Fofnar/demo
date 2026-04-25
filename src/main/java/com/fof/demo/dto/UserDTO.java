package com.fof.demo.dto;

import com.fof.demo.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO représentant les informations d'un utilisateur exposées via l'API.
 *
 * <p>
 * Utilisé pour transférer les données utilisateur vers le frontend Angular
 * sans exposer directement l'entité {@code AppUser}.
 * </p>
 *
 * <p>
 * Contient les informations publiques du profil ainsi que le rôle utilisateur.
 * </p>
 *
 * @author Fodeba Fofana
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long id;

    @NotBlank(message = "Champ Obligatoire")
    @Email
    private String email;

    @NotBlank(message = "Champ Obligatoire")
    private String lastName;

    @NotBlank(message = "Champ Obligatoire")
    private String firstName;

    @Min(0)
    private int age;

    @NotBlank(message = "Champ Obligatoire")
    @Size(min = 6, max = 20)
    private String phone;

    private Role role;
}