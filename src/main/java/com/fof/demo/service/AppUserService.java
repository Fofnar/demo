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

@Service // ==> Rend la classe injectable (utilisable ailleurs)
@RequiredArgsConstructor // ==> Génère un constructeur avec les attributs "final"
public class AppUserService {
    private final PasswordEncoder passwordEncoder;
    private final AppUserRepository userRepository;

    /** Enregistrer un utilisateur */
    public AppUser saveUser(String email, String lastname, String firstname, int age, String rawPassword, String phone){

        if (existsByUsername(email)){
            throw new UserAlreadyExistsException("Email already taken");
        }

        if (existsByPhone(phone)){
            throw new UserAlreadyExistsException("Phone already taken");
        }

        AppUser user = new AppUser();
        user.setEmail(email);
        user.setLastName(lastname);
        user.setFirstName(firstname);
        user.setAge(age);
        user.setPassword(passwordEncoder.encode(rawPassword)); // ==> Le mot de passe est encodé ici (BCrypt)
        user.setPhone(phone);
        user.setRole(Role.USER);  // ==> Par défaut, tous les utilisateurs auront le rôle USER
        return userRepository.save(user); // ==> On enregistre l'utilisateur encodé en base
    }

    /** Promotion ADMIN (méthode dédiée) */
    public void promoteToAdmin(AppUser user){
        user.setRole(Role.ADMIN);
        userRepository.save(user);
    }

    /**  Récupère l’utilisateur par username (email) ou null si absent */
    public AppUser loadUserByUsername(String username) {
        return userRepository.findByEmail(username).orElse(null);
    }

    /**  Compare un mot de passe brut avec un hash BCrypt stocké */
    public boolean matches(String rawPassword, String encodePassword) {
        return passwordEncoder.matches(rawPassword, encodePassword);
    }

    /**  Vérifie si un username (email) existe déjà (utile à l’inscription) */
    public boolean existsByUsername(String username){
        return userRepository.findByEmail(username).isPresent();
    }

    /**  Vérifie si un phone existe déjà  */
    public boolean existsByPhone(String phone){
        return userRepository.findByPhone(phone).isPresent();
    }

    /** Mise à jour utilisateur */
    public UserDTO updateUser(String username, UpdateUserDTO dto) {

        // Récupération de l'utilisateur courant via son email
        AppUser user = loadUserByUsername(username);

        // Si l'utilisateur n'existe pas
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Si l'email est fourni, on met à jour
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            if (!dto.getEmail().equals(user.getEmail()) && existsByUsername(dto.getEmail())) {
                throw new UserAlreadyExistsException("Email already taken");
            }
            user.setEmail(dto.getEmail());
        }

        // Si le nom est fourni, on met à jour
        if (dto.getLastName() != null && !dto.getLastName().isBlank()) {
            user.setLastName(dto.getLastName());
        }

        // Si le prénom est fourni, on met à jour
        if (dto.getFirstName() != null && !dto.getFirstName().isBlank()) {
            user.setFirstName(dto.getFirstName());
        }

        // Si l'âge est fourni correctement, on met à jour
        if (dto.getAge() >= 0) {
            user.setAge(dto.getAge());
        }

        // Si le téléphone est fourni, on met à jour
        if (dto.getPhone() != null && !dto.getPhone().isBlank()) {
            if (!dto.getPhone().equals(user.getPhone()) && existsByPhone(dto.getPhone())) {
                throw new UserAlreadyExistsException("Phone already taken");
            }
            user.setPhone(dto.getPhone());
        }

        // Si un nouveau mot de passe est fourni, on l'encode
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // Sauvegarde finale en base
        AppUser savedUser = userRepository.save(user);

        // Conversion Entity -> DTO
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

    /** Récupérer l'utilisateur*/
    public UserDTO getCurrentUser(String username){

        AppUser user = loadUserByUsername(username);

        if (user == null){
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
