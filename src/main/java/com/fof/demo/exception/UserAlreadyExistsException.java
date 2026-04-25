package com.fof.demo.exception;

/**
 * Exception levée lorsqu'une tentative de création ou de mise à jour
 * d'un utilisateur viole une contrainte d'unicité.
 *
 * <p>
 * Cette exception est typiquement déclenchée lorsque :
 * <ul>
 *     <li>Un email est déjà utilisé par un autre utilisateur</li>
 *     <li>Un numéro de téléphone est déjà utilisé</li>
 * </ul>
 * </p>
 *
 * <p>
 * Elle permet de renvoyer une erreur métier claire au client
 * (ex : HTTP 400 ou 409 selon la configuration du GlobalExceptionHandler).
 * </p>
 *
 * <p>
 * Cette exception s'inscrit dans une logique de validation côté serveur
 * pour garantir l'intégrité des données en base.
 * </p>
 *
 * @author Fodeba Fofana
 */
public class UserAlreadyExistsException extends RuntimeException {

    /**
     * Construit une exception avec un message personnalisé.
     *
     * @param message description de l'erreur (ex : "Email already taken")
     */
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}