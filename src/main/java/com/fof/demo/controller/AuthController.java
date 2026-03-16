package com.fof.demo.controller;

import com.fof.demo.dto.*;
import com.fof.demo.entity.AppUser;
import com.fof.demo.exception.BadCredentialsException;
import com.fof.demo.exception.UserAlreadyExistsException;
import com.fof.demo.security.JwtUtils;
import com.fof.demo.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Contrôleur responsable de l'authentification.
 *
 * Il expose les endpoints pour :
 * - l'inscription des utilisateurs
 * - la connexion
 * - la génération d'un token JWT
 *
 * Toutes les routes de ce contrôleur commencent par /api/auth.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    /**
     * Service métier pour gérer les utilisateurs
     * (création, recherche, validation du mot de passe).
     */
    private final AppUserService userService;

    /**
     * Utilitaire pour générer et manipuler les tokens JWT.
     */
    private final JwtUtils jwtUtils;

    /**
     * Endpoint d'inscription utilisateur.
     *
     * URL : POST /api/auth/register
     *
     * Étapes :
     * 1. Vérifier si l'email existe déjà
     * 2. Créer un nouvel utilisateur
     * 3. Convertir l'entité AppUser en UserDTO
     * 4. Retourner une réponse API structurée
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> register(@RequestBody AuthRequest request) {

        // Vérifie si l'utilisateur existe déjà
        if (userService.existsByUsername(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already taken");
        }

        // Création de l'utilisateur
        AppUser user = userService.saveUser(
                request.getEmail(),
                request.getLastName(),
                request.getFirstName(),
                request.getAge(),
                request.getPassword(),
                request.getPhone()
        );

        // Conversion de l'entité vers DTO (pour éviter d'exposer des données sensibles)
        UserDTO dto = new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getLastName(),
                user.getFirstName(),
                user.getAge(),
                user.getPhone(),
                user.getRole()
        );

        // Création de la réponse API standardisée
        ApiResponse<UserDTO> response = new ApiResponse<>(
                true,
                dto,
                "User registered successfully",
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de connexion utilisateur.
     *
     * URL : POST /api/auth/login
     *
     * Étapes :
     * 1. Récupérer l'utilisateur par email
     * 2. Vérifier le mot de passe
     * 3. Générer un token JWT
     * 4. Retourner le token au client
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest request) {

        // Recherche de l'utilisateur
        AppUser user = userService.loadUserByUsername(request.getEmail());

        // Vérification des identifiants
        if (user == null || !userService.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        // Génération des tokens
        String accessToken = jwtUtils.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtils.generateRefreshToken(user.getEmail());

        AuthResponse authResponse = new AuthResponse(
                accessToken,
                refreshToken
        );

        // Création de la réponse contenant le token
        ApiResponse<AuthResponse> response = new ApiResponse<>(
                true,
                authResponse,
                "Login successful",
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * 🔁 Endpoint permettant de générer un nouveau access token
     * à partir d'un refresh token valide.
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@RequestBody RefreshRequest request){

        String refreshToken = request.getRefreshToken();

        //Vérifie si le token est valide
        if(!jwtUtils.validateJwtToken(refreshToken)){
            throw new BadCredentialsException("Invalid refresh token");
        }

        //récupération de l'email dans le token
        String email = jwtUtils.getUserNameFromJwtToken(refreshToken);

        //généation d'un nouveau token
        String newAccessToken = jwtUtils.generateAccessToken(email);

        AuthResponse authResponse = new AuthResponse(
                newAccessToken,
                refreshToken
        );

        ApiResponse<AuthResponse> response = new ApiResponse<>(
                true,
                authResponse,
                "Token refreshed successfully",
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

}


