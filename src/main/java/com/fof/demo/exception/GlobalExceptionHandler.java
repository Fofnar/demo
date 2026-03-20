package com.fof.demo.exception;

import com.fof.demo.dto.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Gestion globale des exceptions de l'application.
 *
 * Cette classe intercepte les exceptions lancées par les contrôleurs
 * et renvoie une réponse HTTP structurée avec un objet ErrorResponse.
 *
 * Cela permet :
 * - d'avoir des réponses d'erreur cohérentes
 * - d'éviter les stack traces dans les réponses API
 * - de faciliter la gestion des erreurs côté frontend
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gère les RuntimeException génériques.
     *
     * HTTP Status : 400 (Bad Request)
     *
     * Utilisé lorsque la requête envoyée par le client est incorrecte
     * ou lorsqu'une erreur métier simple survient dans l'application.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex){

        ErrorResponse error = new ErrorResponse(
                false,
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Gère l'erreur lorsqu'un utilisateur tente de s'inscrire
     * avec un email déjà existant.
     *
     * HTTP Status : 409 (Conflict)
     *
     * Indique qu'une ressource existe déjà dans la base de données.
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserExists(UserAlreadyExistsException ex){

        ErrorResponse error = new ErrorResponse(
                false,
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(409).body(error);
    }

    /**
     * Gère les erreurs d'authentification.
     *
     * HTTP Status : 401 (Unauthorized)
     *
     * Cette exception est levée lorsque les identifiants fournis
     * (email ou mot de passe) sont incorrects lors de la connexion.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex){

        ErrorResponse error = new ErrorResponse(
                false,
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(401).body(error);
    }

    /**
     * Gère toutes les autres exceptions non prévues.
     *
     * HTTP Status : 500 (Internal Server Error)
     *
     * Permet d'éviter que des erreurs inattendues fassent planter
     * l'application sans réponse claire pour le client.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobal(Exception ex){

        ErrorResponse error = new ErrorResponse(
                false,
                ex.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(500).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDBError(Exception ex){
        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        false,
                        "Missing required field",
                        LocalDateTime.now()
                )
        );
    }

}