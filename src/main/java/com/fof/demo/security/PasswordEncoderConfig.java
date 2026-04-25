package com.fof.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration du composant d'encodage des mots de passe.
 *
 * <p>
 * Cette classe expose un bean {@link PasswordEncoder} utilisé dans toute l'application
 * pour sécuriser les mots de passe des utilisateurs.
 * </p>
 *
 * <p>
 * Implémentation choisie :
 * <ul>
 *     <li>{@link BCryptPasswordEncoder} : algorithme de hachage sécurisé avec salage intégré</li>
 * </ul>
 * </p>
 *
 * <p>
 * Avantages de BCrypt :
 * <ul>
 *     <li>Résistant aux attaques par force brute</li>
 *     <li>Gestion automatique du "salt"</li>
 *     <li>Adaptatif (coût configurable)</li>
 * </ul>
 * </p>
 *
 * <p>
 * Ce bean est utilisé notamment dans :
 * <ul>
 *     <li>La création des utilisateurs (encodage du mot de passe)</li>
 *     <li>La vérification des identifiants lors de l'authentification</li>
 * </ul>
 * </p>
 *
 * @author Fodeba Fofana
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * Fournit une instance de {@link PasswordEncoder} basée sur BCrypt.
     *
     * @return un encodeur de mot de passe sécurisé
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);  // coût de hashing (strength)
    }
}