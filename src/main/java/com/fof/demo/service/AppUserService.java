package com.fof.demo.service;

import com.fof.demo.entity.AppUser;
import com.fof.demo.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service // ==> Rend la classe injectable (utilisable ailleurs)
@RequiredArgsConstructor // ==> Génère un constructeur avec les attributs "final"
public class AppUserService {
    private final PasswordEncoder passwordEncoder;
    private final AppUserRepository userRepository;

    public AppUser saveUser(String username, String rawPassword){

        if (existsByUsername(username)){
            return  loadUserByUsername(username);
        }

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword)); // ==> Le mot de passe est encodé ici (BCrypt)
        user.setRole("USER");
        return userRepository.save(user); // ==> On enregistre l'utilisateur encodé en base
    }

    /** ✅ Récupère l’utilisateur par username (ou null si absent) */
    public AppUser loadUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    /** ✅ Compare un mot de passe brut avec un hash BCrypt stocké */
    public boolean matches(String rawPassword, String encodePassword) {
        return passwordEncoder.matches(rawPassword, encodePassword);
    }

    /** ✅ Vérifie si un username existe déjà (utile à l’inscription) */
    public boolean existsByUsername(String username){
        return userRepository.findByUsername(username).isPresent();
    }
}
