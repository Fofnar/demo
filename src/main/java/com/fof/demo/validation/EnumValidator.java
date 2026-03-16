package com.fof.demo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Annotation de validation personnalisée permettant de vérifier
 * qu'une valeur String correspond bien à une constante d'un Enum donné.
 *
 * Cette annotation peut être utilisée dans les DTO pour garantir que
 * les valeurs envoyées par le client appartiennent bien à un Enum défini.
 *
 * Exemple d'utilisation :
 *
 *     @EnumValidator(enumClass = Role.class)
 *     private String role;
 *
 * Si la valeur envoyée ne correspond pas à une constante de Role,
 * la validation échouera automatiquement.
 */
@Documented
@Constraint(validatedBy = EnumValidatorImpl.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumValidator {

    /**
     * Enum à utiliser pour la validation.
     *
     * Exemple :
     * enumClass = Role.class
     */
    Class<? extends Enum<?>> enumClass();

    /**
     * Message d'erreur retourné lorsque la validation échoue.
     */
    String message() default "Invalid value";

    /**
     * Permet de regrouper différentes validations
     */
    Class<?>[] groups() default {};

    /**
     * Permet d'associer des métadonnées à la contrainte de validation.
     * Utilisé dans des cas avancés de gestion d'erreurs.
     */
    Class<? extends Payload>[] payload() default {};
}