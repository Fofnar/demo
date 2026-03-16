package com.fof.demo.specification;

import com.fof.demo.entity.SaleEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

/**
 * Classe utilitaire permettant de construire des filtres
 * dynamiques pour les requêtes sur l'entité SaleEntity.
 *
 * Chaque méthode retourne une Specification qui représente
 * une condition SQL pouvant être combinée avec d'autres
 * Specifications (via AND / OR).
 *
 * Ces Specifications sont utilisées par Spring Data JPA
 * pour générer automatiquement la clause WHERE d'une requête.
 */
public class SaleSpecification {

    /**
     * Filtre les ventes par nom de produit.
     *
     * Si aucun produit n'est fourni (null),
     * aucun filtre n'est appliqué.
     *
     * Exemple SQL généré :
     * SELECT * FROM sales WHERE product = 'Laptop'
     */
    public static Specification<SaleEntity> hasProduct(String product){

        return (root, query, cb) ->
                product == null ? null :
                        cb.equal(root.get("product"), product);
    }

    /**
     * Filtre les ventes dont le stock est inférieur
     * à une valeur donnée.
     *
     * Exemple :
     * stockLessThan(10)
     *
     * SQL généré :
     * SELECT * FROM sales WHERE stock < 10
     *
     * Si stock est null, aucun filtre n'est appliqué.
     */
    public static Specification<SaleEntity> stockLessThan(Integer stock){

        return (root, query, cb) ->
                stock == null ? null :
                        cb.lessThan(root.get("stock"), stock);
    }

    /**
     * Filtre les ventes dont la date est postérieure
     * à une date donnée.
     *
     * Exemple :
     * dateAfter(2025-01-01)
     *
     * SQL généré :
     * SELECT * FROM sales WHERE sale_date > '2025-01-01'
     *
     * Si aucune date n'est fournie, aucun filtre
     * n'est appliqué.
     */
    public static Specification<SaleEntity> dateAfter(LocalDateTime date){

        return (root, query, cb) ->
                date == null ? null :
                        cb.greaterThan(root.get("saleDate"), date);
    }

    /**
     * Filtre les ventes dont le stock est compris
     * entre deux valeurs.
     *
     * Exemple :
     * stockBetween(5, 20)
     *
     * SQL généré :
     * SELECT * FROM sales WHERE stock BETWEEN 5 AND 20
     *
     * Si l'une des deux valeurs est null,
     * aucun filtre n'est appliqué.
     */
    public static Specification<SaleEntity> stockBetween(Integer minStock, Integer maxStock){

        return (root, query, cb) -> {

            if(minStock == null || maxStock == null){
                return null;
            }

            return cb.between(root.get("stock"), minStock, maxStock);
        };
    }

    /**
     * Filtre les ventes dont la date est comprise
     * entre deux dates.
     *
     * Exemple :
     * dateBetween(2025-01-01, 2025-03-01)
     *
     * SQL généré :
     * SELECT * FROM sales
     * WHERE sale_date BETWEEN '2025-01-01' AND '2025-03-01'
     *
     * Si l'une des deux dates est null,
     * aucun filtre n'est appliqué.
     */
    public static Specification<SaleEntity> dateBetween(LocalDateTime startDate, LocalDateTime endDate){

        return (root, query, cb) -> {

            if(startDate == null || endDate == null){
                return null;
            }

            return cb.between(root.get("saleDate"), startDate, endDate);
        };
    }

    /**
     * Filtre les ventes dont le prix est compris
     * entre deux valeurs.
     *
     * Exemple :
     * priceBetween(100, 500)
     *
     * SQL généré :
     * SELECT * FROM sales WHERE price BETWEEN 100 AND 500
     *
     * Si l'une des deux valeurs est null,
     * aucun filtre n'est appliqué.
     */
    public static Specification<SaleEntity> priceBetween(Double minPrice, Double maxPrice){

        return (root, query, cb) -> {

            if(minPrice == null || maxPrice == null){
                return null;
            }

            return cb.between(root.get("price"), minPrice, maxPrice);
        };
    }

    /**
     * Recherche textuelle dans le champ product de l'entité SaleEntity.
     *
     * Cette méthode permet d'implémenter une barre de recherche
     * capable de filtrer les résultats en fonction d'un mot-clé.
     *
     * La recherche est effectuée sur la colonne product
     *
     * Exemple :
     * keyword = "lap"
     *
     * SQL généré :
     * SELECT * FROM sales
     * WHERE LOWER(product) LIKE '%lap%'
     *
     * Si aucun mot-clé n'est fourni, aucun filtre n'est appliqué.
     */

    public static Specification<SaleEntity> keywordSearch(String keyword){

        return (root, query, cb) -> {

            if(keyword == null || keyword.trim().isEmpty()){
                return null;
            }

            String pattern = "%" + keyword.toLowerCase() + "%";

            return cb.like(cb.lower(root.get("product")), pattern);
        };
    }



}