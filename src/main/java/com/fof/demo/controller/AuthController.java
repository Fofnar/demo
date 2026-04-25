package com.fof.demo.controller;

import com.fof.demo.dto.*;
import com.fof.demo.entity.AppUser;
import com.fof.demo.exception.BadCredentialsException;
import com.fof.demo.security.JwtUtils;
import com.fof.demo.service.AppUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Contrôleur responsable de l'authentification.
 *
 * Il expose les endpoints pour :
 * - l'inscription des utilisateurs
 * - la connexion
 * - le renouvellement d'un token JWT
 *
 * Toutes les routes de ce contrôleur commencent par /api/auth.
 * @author Fodeba Fofana
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
     *
     * @param request informations nécessaires à l'inscription
     * @return utilisateur créé encapsulé dans ApiResponse
     */
    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account after checking that the email is not already taken."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Email already taken"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User registration data",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = RegisterRequest.class),
                    examples = @ExampleObject(
                            name = "Register example",
                            value = """
                                    {
                                      "email": "fofnarbf@gmail.com",
                                      "lastName": "Fofana",
                                      "firstName": "Fodeba",
                                      "age": 20,
                                      "password": "Fodeba123",
                                      "phone": "03010101010101010101"
                                    }
                                    """
                    )
            )
    )
    public ResponseEntity<ApiResponse<UserDTO>> register(@RequestBody @Valid RegisterRequest request) {

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
     *
     * @param request identifiants de connexion
     * @return access token et refresh token
     */
    @PostMapping("/login")
    @Operation(
            summary = "Login user",
            description = "Authenticates a user and returns an access token and a refresh token."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User login data",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = LoginRequest.class),
                    examples = @ExampleObject(
                            name = "Login example",
                            value = """
                                    {
                                      "email": "fofnarbf@gmail.com",
                                      "password": "Fodeba123"
                                    }
                                    """
                    )
            )
    )
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody @Valid LoginRequest request) {

        // Recherche de l'utilisateur
        AppUser user = userService.loadUserByUsername(request.getEmail());

        // Vérification des identifiants
        if (user == null || !userService.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        // Génération des tokens
        String accessToken = jwtUtils.generateAccessToken(
                user.getEmail(),
                user.getRole().name()
        );
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
     * Endpoint permettant de générer un nouvel access token
     * à partir d'un refresh token valide.
     *
     * @param request refresh token envoyé par le client
     * @return nouveau access token et refresh token inchangé
     */
    @PostMapping("/refresh")
    @Operation(
            summary = "Refresh access token",
            description = "Generates a new access token from a valid refresh token."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Token refreshed successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid refresh token"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Refresh token payload",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = RefreshRequest.class),
                    examples = @ExampleObject(
                            name = "Refresh example",
                            value = """
                                    {
                                      "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmb2ZuYXJiZkBnbWFpbC5jb20iLCJpYXQiOjE3NzM5Mzk4MzUsImV4cCI6MTc3NDU0NDYzNX0.SbhWke6jPm8rWemWSeLbSVYfU1fq37TJtbE0KbywPGY"
                                    }
                                    """
                    )
            )
    )
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@RequestBody @Valid RefreshRequest request) {

        String refreshToken = request.getRefreshToken();

        // Vérifie si le token est valide
        if (!jwtUtils.validateJwtToken(refreshToken)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        // Récupération de l'email dans le token
        String email = jwtUtils.getUserNameFromJwtToken(refreshToken);
        AppUser user = userService.loadUserByUsername(email);

        // Génération d'un nouveau token
        String newAccessToken = jwtUtils.generateAccessToken(email, user.getRole().name());

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