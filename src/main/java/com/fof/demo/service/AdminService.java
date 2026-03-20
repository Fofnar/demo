package com.fof.demo.service;

import com.fof.demo.dto.AdminStatsDTO;
import com.fof.demo.entity.SaleEntity;
import com.fof.demo.repository.AppUserRepository;
import com.fof.demo.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    // Repository pour les utilisateurs
    private final AppUserRepository userRepository;

    //Repository pour les ventes
    private final SaleRepository saleRepository;

    public AdminStatsDTO getStats() {
        //Nombre total d'utilisateur
        long totalUsers = userRepository.count();

        //nombre total de vente
        long totalSales = saleRepository.count();

        //date début de la journée
        LocalDateTime todayStart =LocalDateTime.now().withHour(0).withMinute(0);

        //Vente d'aujourd'hui
        long salesToday = saleRepository.findBySaleDateAfter(todayStart).size();

        // récupérer toutes les ventes
        List<SaleEntity> sales = saleRepository.findAll();

        // nombre de produits différents
        long totalProducts = sales.stream()
                .map(SaleEntity::getProduct)
                .distinct()
                .count();

        //produits avec les stocks faible ( stock < 10)
        long lowSotockProducts = sales.stream()
                .filter(s-> s.getStock() < 10)
                .map(SaleEntity::getProduct)
                .distinct()
                .count();

        //Retourner les statistiques
        return new AdminStatsDTO(
                totalUsers,
                totalSales,
                salesToday,
                totalProducts,
                lowSotockProducts
        );
    }

}
