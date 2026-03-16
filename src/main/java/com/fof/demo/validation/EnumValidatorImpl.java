package com.fof.demo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

/**
 * Implémentation de la logique de validation pour l'annotation {@link EnumValidator}.
 *
 * Cette classe vérifie qu'une valeur String correspond bien à une constante
 * définie dans l'Enum spécifié dans l'annotation.
 *
 * Exemple :
 * Si l'enum contient :
 *
 *     USER
 *     ADMIN
 *
 * Alors seules ces valeurs seront acceptées.
 */
public class EnumValidatorImpl implements ConstraintValidator<EnumValidator, String> {

    /**
     * Classe de l'enum utilisée pour la validation.
     * Elle est récupérée depuis l'annotation EnumValidator.
     */
    private Class<? extends Enum<?>> enumClass;

    /**
     * Méthode appelée lors de l'initialisation du validator.
     *
     * Elle permet de récupérer les paramètres de l'annotation
     * (notamment la classe Enum à utiliser).
     */
    @Override
    public void initialize(EnumValidator constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
    }

    /**
     * Méthode principale de validation appelée automatiquement
     * par le framework Jakarta Validation.
     *
     * @param value valeur à valider
     * @param context contexte de validation
     * @return true si la valeur est valide, false sinon
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        /**
         * Si la valeur est null, on considère la validation comme valide.
         * La vérification du null doit être gérée séparément avec @NotNull
         * si nécessaire.
         */
        if (value == null) {
            return true;
        }

        /**
         * Récupère toutes les constantes de l'enum puis vérifie
         * si la valeur envoyée correspond à l'une d'entre elles.
         */
        return Arrays.stream(enumClass.getEnumConstants())
                .anyMatch(e -> e.name().equals(value));
    }
}