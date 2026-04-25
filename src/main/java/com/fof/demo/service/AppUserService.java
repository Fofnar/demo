package com.fof.demo.service;

import com.fof.demo.dto.UpdateUserDTO;
import com.fof.demo.dto.UserDTO;
import com.fof.demo.entity.AppUser;
import com.fof.demo.enums.Role;
import com.fof.demo.exception.UserAlreadyExistsException;
import com.fof.demo.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service métier dédié à la gestion des utilisateurs.
 *
 * <p>
 * Ce service centralise les opérations principales liées aux comptes utilisateurs :
 * inscription, authentification métier, mise à jour du profil, récupération du profil courant
 * et gestion du rôle administrateur.
 * </p>
 *
 * <p>
 * Il applique également plusieurs règles de sécurité importantes :
 * <ul>
 *     <li>Encodage des mots de passe avec {@link PasswordEncoder}</li>
 *     <li>Vérification de l'unicité des emails</li>
 *     <li>Vérification de l'unicité des numéros de téléphone</li>
 *     <li>Attribution du rôle {@link Role#USER} par défaut</li>
 * </ul>
 * </p>
 *
 * @author Fodeba Fofana
 */
@Service
@RequiredArgsConstructor
public class AppUserService {

    /**
     * Encodeur de mots de passe utilisé pour sécuriser les mots de passe utilisateurs.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Repository permettant l'accès aux données des utilisateurs.
     */
    private final AppUserRepository userRepository;

    /**
     * Enregistre un nouvel utilisateur en base de données.
     *
     * <p>
     * L'utilisateur est créé avec le rôle {@link Role#USER} par défaut.
     * Avant l'enregistrement, le service vérifie que l'email et le numéro de téléphone
     * ne sont pas déjà utilisés, puis encode le mot de passe.
     * </p>
     *
     * @param email adresse email de l'utilisateur
     * @param lastname nom de famille de l'utilisateur
     * @param firstname prénom de l'utilisateur
     * @param age âge de l'utilisateur
     * @param rawPassword mot de passe brut fourni lors de l'inscription
     * @param phone numéro de téléphone de l'utilisateur
     * @return l'utilisateur enregistré
     * @throws UserAlreadyExistsException si l'email ou le téléphone existe déjà
     */
    public AppUser saveUser(String email, String lastname, String firstname, int age, String rawPassword, String phone) {

        if (existsByUsername(email)) {
            throw new UserAlreadyExistsException("Email already taken");
        }

        if (existsByPhone(phone)) {
            throw new UserAlreadyExistsException("Phone already taken");
        }

        AppUser user = new AppUser();
        user.setEmail(email);
        user.setLastName(lastname);
        user.setFirstName(firstname);
        user.setAge(age);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setPhone(phone);
        user.setRole(Role.USER);

        return userRepository.save(user);
    }

    /**
     * Promeut un utilisateur existant au rôle administrateur.
     *
     * @param user utilisateur à promouvoir
     */
    public void promoteToAdmin(AppUser user) {
        user.setRole(Role.ADMIN);
        userRepository.save(user);
    }

    /**
     * Recherche un utilisateur à partir de son email.
     *
     * <p>
     * Dans cette application, l'email est utilisé comme identifiant principal
     * pour l'authentification.
     * </p>
     *
     * @param username email de l'utilisateur
     * @return l'utilisateur trouvé, ou {@code null} si aucun utilisateur ne correspond
     */
    public AppUser loadUserByUsername(String username) {
        return userRepository.findByEmail(username).orElse(null);
    }

    /**
     * Compare un mot de passe brut avec un mot de passe encodé.
     *
     * @param rawPassword mot de passe brut fourni par l'utilisateur
     * @param encodedPassword mot de passe encodé stocké en base
     * @return {@code true} si les mots de passe correspondent, sinon {@code false}
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * Vérifie si un email est déjà utilisé par un utilisateur.
     *
     * @param username email à vérifier
     * @return {@code true} si l'email existe déjà, sinon {@code false}
     */
    public boolean existsByUsername(String username) {
        return userRepository.findByEmail(username).isPresent();
    }

    /**
     * Vérifie si un numéro de téléphone est déjà utilisé par un utilisateur.
     *
     * @param phone numéro de téléphone à vérifier
     * @return {@code true} si le téléphone existe déjà, sinon {@code false}
     */
    public boolean existsByPhone(String phone) {
        return userRepository.findByPhone(phone).isPresent();
    }

    /**
     * Met à jour les informations du profil utilisateur courant.
     *
     * <p>
     * Seuls les champs fournis dans le DTO sont mis à jour.
     * Les champs vides ou {@code null} sont ignorés.
     * Si un nouvel email ou téléphone est fourni, son unicité est vérifiée.
     * Si un nouveau mot de passe est fourni, il est encodé avant sauvegarde.
     * </p>
     *
     * @param username email actuel de l'utilisateur connecté
     * @param dto données de mise à jour du profil
     * @return un {@link UserDTO} représentant l'utilisateur mis à jour
     * @throws RuntimeException si l'utilisateur courant est introuvable
     * @throws UserAlreadyExistsException si le nouvel email ou téléphone existe déjà
     */
    public UserDTO updateUser(String username, UpdateUserDTO dto) {

        // Récupération de l'utilisateur courant via son email
        AppUser user = loadUserByUsername(username);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Mise à jour de l'email avec contrôle d'unicité
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            if (!dto.getEmail().equals(user.getEmail()) && existsByUsername(dto.getEmail())) {
                throw new UserAlreadyExistsException("Email already taken");
            }
            user.setEmail(dto.getEmail());
        }

        // Mise à jour du nom
        if (dto.getLastName() != null && !dto.getLastName().isBlank()) {
            user.setLastName(dto.getLastName());
        }

        // Mise à jour du prénom
        if (dto.getFirstName() != null && !dto.getFirstName().isBlank()) {
            user.setFirstName(dto.getFirstName());
        }

        // Mise à jour de l'âge
        if (dto.getAge() >= 0) {
            user.setAge(dto.getAge());
        }

        // Mise à jour du téléphone avec contrôle d'unicité
        if (dto.getPhone() != null && !dto.getPhone().isBlank()) {
            if (!dto.getPhone().equals(user.getPhone()) && existsByPhone(dto.getPhone())) {
                throw new UserAlreadyExistsException("Phone already taken");
            }
            user.setPhone(dto.getPhone());
        }

        // Mise à jour du mot de passe avec encodage sécurisé
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        AppUser savedUser = userRepository.save(user);

        return new UserDTO(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getLastName(),
                savedUser.getFirstName(),
                savedUser.getAge(),
                savedUser.getPhone(),
                savedUser.getRole()
        );
    }

    /**
     * Récupère les informations du profil utilisateur courant.
     *
     * @param username email de l'utilisateur connecté
     * @return un {@link UserDTO} contenant les informations publiques du profil
     * @throws RuntimeException si l'utilisateur est introuvable
     */
    public UserDTO getCurrentUser(String username) {

        AppUser user = loadUserByUsername(username);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getLastName(),
                user.getFirstName(),
                user.getAge(),
                user.getPhone(),
                user.getRole()
        );
    }
}