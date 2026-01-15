package com.fof.demo.auth;

import com.fof.demo.entity.AppUser;
import com.fof.demo.security.JwtUtils;
import com.fof.demo.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/auth") // Toutes les routes commencent par /api/auth
@RequiredArgsConstructor // Génère un constructeur automatiquement pour les champs 'final'
public class AuthController {

    private final AppUserService userService; // Sert à enregistrer ou chercher un utilisateur
    private final JwtUtils jwtUtils;

    /** 🔐 Inscription */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest request) {
        if (userService.existsByUsername(request.getUsername())) {
            return ResponseEntity.status(409).body("Username déjà pris");
        }
        AppUser user = userService.saveUser(request.getUsername(),request.getPassword());
        return ResponseEntity.ok("Utilisateur créé : " + user.getUsername());
    }

    /** 🔓 Connexion */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        AppUser user = userService.loadUserByUsername(request.getUsername());
        if (user == null || !userService.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Identifiants invalides");
        }
        String token = jwtUtils.generateJwtToken(user.getUsername());
        // retourne le token dans un objet JSON
        return ResponseEntity.ok(Map.of("message", "connexion réussie", "token", token));
    }


}
