package com.fof.demo.enums;

/**
 * Enumération représentant les rôles des utilisateurs dans l'application.
 *
 * <p>
 * Les rôles définissent les niveaux d'autorisation et d'accès aux fonctionnalités :
 * <ul>
 *     <li>{@link #USER} : utilisateur standard avec accès aux fonctionnalités de base</li>
 *     <li>{@link #ADMIN} : administrateur avec accès aux fonctionnalités avancées (gestion, statistiques, etc.)</li>
 * </ul>
 * </p>
 *
 * <p>
 * Ce rôle est utilisé dans le système de sécurité (Spring Security)
 * pour contrôler l'accès aux endpoints via des règles d'autorisation.
 * </p>
 *
 * <p>
 * Il est généralement stocké en base de données sous forme de chaîne de caractères
 * (via {@code EnumType.STRING}) pour plus de lisibilité et de stabilité.
 * </p>
 *
 * @author Fodeba Fofana
 */
public enum Role {

    /**
     * Rôle par défaut attribué à tout nouvel utilisateur.
     */
    USER,

    /**
     * Rôle administrateur avec privilèges étendus.
     */
    ADMIN
}