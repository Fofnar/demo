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
            throw new UserAlreadyExistsException("User with this email already exists");
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

    /** Mise à jour utilisateur */
    public UserDTO updateUser(String username, UpdateUserDTO dto){

        // récupérer utilisateur actuel
        AppUser user = loadUserByUsername(username);

        if (user == null){
            throw new RuntimeException("User not found");
        }

        // mise à jour des champs autorisés
        user.setEmail(dto.getEmail());
        user.setLastName(dto.getLastName());
        user.setFirstName(dto.getFirstName());
        user.setAge(dto.getAge());
        user.setPhone(dto.getPhone());

        // sauvegarde
        AppUser savedUser = userRepository.save(user);

        // conversion Entity -> DTO
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
