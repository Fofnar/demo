package com.fof.demo.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * Configuration activant la sécurité au niveau des méthodes.
 *
 * <p>
 * Cette classe permet d'utiliser les annotations de sécurité directement
 * sur les méthodes des contrôleurs ou services, comme :
 * <ul>
 *     <li>{@code @PreAuthorize}</li>
 *     <li>{@code @PostAuthorize}</li>
 *     <li>{@code @Secured}</li>
 * </ul>
 * </p>
 *
 * <p>
 * Elle complète la configuration globale de Spring Security en permettant
 * un contrôle plus précis des accès selon les rôles ou permissions.
 * </p>
 *
 * @author Fodeba Fofana
 */
@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig {
}