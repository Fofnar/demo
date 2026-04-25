package com.fof.demo.service;

import com.fof.demo.dto.AdminStatsDTO;
import com.fof.demo.entity.SaleEntity;
import com.fof.demo.repository.AppUserRepository;
import com.fof.demo.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service métier dédié à l'administration.
 *
 * <p>
 * Fournit des statistiques globales sur l'application, incluant :
 * <ul>
 *     <li>Le nombre total d'utilisateurs</li>
 *     <li>Le nombre total de ventes</li>
 *     <li>Le nombre de ventes effectuées aujourd'hui</li>
 *     <li>Le nombre de produits distincts</li>
 *     <li>Le nombre de produits en stock faible</li>
 * </ul>
 * </p>
 *
 * <p>
 * Ces données sont principalement utilisées pour alimenter un dashboard administrateur.
 * </p>
 *
 * @author Fodeba Fofana
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    /**
     * Repository permettant l'accès aux données des utilisateurs.
     */
    private final AppUserRepository userRepository;

    /**
     * Repository permettant l'accès aux données des ventes.
     */
    private final SaleRepository saleRepository;

    /**
     * Calcule et retourne les statistiques globales de l'application.
     *
     * <p>
     * Les statistiques incluent :
     * <ul>
     *     <li>Total des utilisateurs</li>
     *     <li>Total des ventes</li>
     *     <li>Ventes réalisées aujourd'hui</li>
     *     <li>Nombre de produits distincts</li>
     *     <li>Nombre de produits avec un stock faible (&lt; 10)</li>
     * </ul>
     * </p>
     *
     * @return un objet {@link AdminStatsDTO} contenant l'ensemble des indicateurs calculés
     */
    public AdminStatsDTO getStats() {
        // Nombre total d'utilisateurs
        long totalUsers = userRepository.count();

        // Nombre total de ventes
        long totalSales = saleRepository.count();

        // Date de début de la journée (00:00)
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0);

        // Nombre de ventes effectuées aujourd'hui
        long salesToday = saleRepository.findBySaleDateAfter(todayStart).size();

        // Récupération de toutes les ventes
        List<SaleEntity> sales = saleRepository.findAll();

        // Nombre de produits distincts
        long totalProducts = sales.stream()
                .map(SaleEntity::getProduct)
                .distinct()
                .count();

        // Nombre de produits avec un stock faible (stock < 10)
        long lowStockProducts = sales.stream()
                .filter(s -> s.getStock() < 10)
                .map(SaleEntity::getProduct)
                .distinct()
                .count();

        // Construction et retour des statistiques
        return new AdminStatsDTO(
                totalUsers,
                totalSales,
                salesToday,
                totalProducts,
                lowStockProducts
        );
    }

}