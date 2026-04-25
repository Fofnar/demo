package com.fof.demo.repository;

import com.fof.demo.entity.SaleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository d'accès aux données des ventes.
 *
 * <p>
 * Cette interface permet d'effectuer les opérations CRUD sur l'entité {@link SaleEntity}
 * ainsi que des requêtes spécifiques utilisées pour :
 * <ul>
 *     <li>L'analyse des ventes (KPI, tendances)</li>
 *     <li>La détection d'anomalies</li>
 *     <li>Les prédictions de ventes</li>
 *     <li>Le filtrage par utilisateur ou produit</li>
 * </ul>
 * </p>
 *
 * <p>
 * Elle supporte également les requêtes dynamiques via {@link JpaSpecificationExecutor},
 * permettant de construire des filtres avancés (dates, prix, stock, etc.).
 * </p>
 *
 * <p>
 * Ce repository alimente directement :
 * <ul>
 *     <li>Le dashboard Angular</li>
 *     <li>Le microservice IA (FastAPI)</li>
 * </ul>
 * </p>
 *
 * @author Fodeba Fofana
 */
public interface SaleRepository extends
        JpaRepository<SaleEntity, Long>,
        JpaSpecificationExecutor<SaleEntity> {

    /**
     * Récupère toutes les ventes effectuées après une date donnée.
     *
     * <p>
     * Utilisé pour :
     * <ul>
     *     <li>Les statistiques journalières</li>
     *     <li>Les analyses temporelles</li>
     * </ul>
     * </p>
     *
     * @param date date de référence
     * @return liste des ventes postérieures à la date
     */
    List<SaleEntity> findBySaleDateAfter(LocalDateTime date);

    /**
     * Récupère les ventes associées à un produit spécifique.
     *
     * @param product nom du produit
     * @return liste des ventes correspondant au produit
     */
    List<SaleEntity> findByProduct(String product);

    /**
     * Récupère toutes les ventes associées à un utilisateur via son email.
     *
     * <p>
     * Permet d'isoler les données d'un utilisateur dans une logique
     * proche du multi-tenant (scopage par utilisateur).
     * </p>
     *
     * @param email email de l'utilisateur
     * @return liste des ventes associées
     */
    List<SaleEntity> findByUserEmail(String email);

    /**
     * Récupère les ventes d’un utilisateur avec pagination.
     *
     * <p>
     * Utilisé pour :
     * <ul>
     *     <li>Les interfaces frontend paginées</li>
     *     <li>Optimiser les performances sur de gros volumes de données</li>
     * </ul>
     * </p>
     *
     * @param email email de l'utilisateur
     * @param pageable informations de pagination
     * @return une page de ventes associées à l'utilisateur
     */
    Page<SaleEntity> findByUserEmail(String email, Pageable pageable);
}