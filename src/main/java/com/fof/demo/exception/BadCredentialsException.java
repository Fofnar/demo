package com.fof.demo.exception;

/**
 * Exception personnalisée lancée lorsque les identifiants
 * fournis par un utilisateur sont incorrects.
 *
 * Exemple :
 * - email inexistant
 * - mot de passe incorrect
 *
 * Cette exception est interceptée par GlobalExceptionHandler
 * pour renvoyer une réponse HTTP 401 (Unauthorized).
 */
public class BadCredentialsException extends RuntimeException {

  public BadCredentialsException(String message) {
    super(message);
  }

}