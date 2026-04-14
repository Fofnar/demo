package com.fof.demo.service; // Déclare le package du service

import com.fof.demo.dto.PagedResponse; // Modèle paginé personnalisé
import com.fof.demo.dto.SaleResponseDTO; // DTO de réponse des ventes
import com.fof.demo.entity.AppUser; // Entité utilisateur liée à une vente
import com.fof.demo.entity.SaleEntity; // Entité JPA des ventes
import com.fof.demo.repository.SaleRepository; // Repository JPA des ventes
import com.fof.demo.specification.SaleSpecification; // Specifications pour les filtres dynamiques
import lombok.RequiredArgsConstructor; // Génère le constructeur pour les champs final

import org.springframework.data.domain.Page; // Représente une page Spring Data
import org.springframework.data.domain.PageRequest; // Permet de demander une page précise
import org.springframework.data.domain.Pageable; // Représente les paramètres de pagination
import org.springframework.data.domain.Sort; // Permet de définir un tri
import org.springframework.data.jpa.domain.Specification; // Permet de combiner des filtres
import org.springframework.stereotype.Service; // Déclare un service Spring

import java.time.LocalDateTime; // Type date/heure backend
import java.util.ArrayList; // Liste modifiable
import java.util.List; // Interface de liste
import java.util.stream.Collectors; // Permet de convertir les entités en DTO

/**
 * Service métier responsable de la gestion des ventes.
 *
 * Cette classe encapsule la logique d'accès aux données
 * et communique avec le repository pour effectuer les
 * opérations CRUD et les recherches avancées.
 *
 * Elle supporte :
 * - création de ventes
 * - suppression
 * - recherche par filtres dynamiques
 * - pagination des résultats
 */
@Service // Rend la classe injectable par Spring
@RequiredArgsConstructor // Génère le constructeur avec les dépendances final
public class SaleService { // Service métier des ventes

    private final SaleRepository saleRepository; // Accès à la base de données

    /**
     * Création d'une nouvelle vente.
     *
     * Les informations reçues sont utilisées pour construire
     * une nouvelle entité SaleEntity qui sera persistée
     * dans la base de données.
     */
    public SaleResponseDTO createSale( // Retourne un DTO de réponse
                                       LocalDateTime saleDate, // Date de la vente
                                       String product, // Nom du produit
                                       Double price, // Prix unitaire
                                       int quantity, // Quantité vendue
                                       int stock, // Stock restant
                                       AppUser user // Utilisateur propriétaire de la vente
    ) {
        SaleEntity saleEntity = new SaleEntity(); // Crée une nouvelle entité vente

        saleEntity.setSaleDate(saleDate); // Affecte la date
        saleEntity.setProduct(product); // Affecte le produit
        saleEntity.setPrice(price); // Affecte le prix
        saleEntity.setQuantity(quantity); // Affecte la quantité
        saleEntity.setStock(stock); // Affecte le stock
        saleEntity.setUser(user); // Affecte l'utilisateur lié à la vente

        SaleEntity savedSale = saleRepository.save(saleEntity); // Sauvegarde l'entité en base

        return SaleResponseDTO.fromEntity(savedSale); // Convertit l'entité en DTO
    }

    /**
     * Retourne toutes les ventes sous forme de DTO.
     *
     * Cette méthode est adaptée aux endpoints API,
     * mais elle reste coûteuse pour de très grands volumes.
     */
    public List<SaleResponseDTO> findAll() { // Retourne des DTOs
        return saleRepository.findAll() // Récupère toutes les entités
                .stream() // Transforme la liste en flux
                .map(SaleResponseDTO::fromEntity) // Convertit chaque entité en DTO
                .collect(Collectors.toList()); // Reconstruit la liste finale
    }

    /**
     * Retourne toutes les ventes sous forme d'entités.
     *
     * Cette méthode doit rester réservée aux usages internes,
     * par exemple pour un traitement IA ou un batch métier.
     */
    public List<SaleEntity> findAllEntities() { // Retourne les entités brutes
        return saleRepository.findAll(); // Charge toutes les ventes depuis la base
    }

    /**
     * Retourne les ventes par lots pour limiter la mémoire consommée.
     *
     * Cette méthode permet de traiter une grande table par pages,
     * ce qui reste plus sain qu'un chargement complet.
     */
    public List<SaleEntity> findAllEntitiesInBatches(int pageSize) { // Retourne les entités par batch
        List<SaleEntity> allSales = new ArrayList<>(); // Liste finale des ventes
        int page = 0; // Numéro de page courant
        Page<SaleEntity> batch; // Variable contenant chaque lot

        do { // Boucle au moins une fois
            batch = saleRepository.findAll( // Récupère une page d'entités
                    PageRequest.of(page, pageSize, Sort.by("saleDate").ascending()) // Pagination + tri
            );

            allSales.addAll(batch.getContent()); // Ajoute le lot courant à la liste finale
            page++; // Passe au lot suivant
        } while (batch.hasNext()); // Continue tant qu'il reste des pages

        return allSales; // Retourne toutes les entités collectées
    }

    /**
     * Recherche une vente par son identifiant.
     *
     * Si aucune vente n'est trouvée,
     * une exception est levée.
     */
    public SaleResponseDTO findById(Long id) { // Retourne un DTO
        SaleEntity sale = saleRepository.findById(id) // Cherche l'entité
                .orElseThrow(() -> new RuntimeException("Sale not found")); // Erreur si absent

        return SaleResponseDTO.fromEntity(sale); // Conversion en DTO
    }

    /**
     * Suppression d'une vente par son identifiant.
     */
    public void deleteById(Long id) { // Supprime une vente
        saleRepository.deleteById(id); // Exécute la suppression
    }

    /**
     * Recherche des ventes après une certaine date.
     */
    public List<SaleResponseDTO> findSalesAfter(LocalDateTime date) { // Retourne des DTOs
        return saleRepository.findBySaleDateAfter(date) // Recherche les entités
                .stream() // Flux de conversion
                .map(SaleResponseDTO::fromEntity) // Conversion en DTO
                .collect(Collectors.toList()); // Liste finale
    }

    /**
     * Recherche toutes les ventes d'un produit spécifique.
     */
    public List<SaleResponseDTO> findSalesByProduct(String product) { // Retourne des DTOs
        return saleRepository.findByProduct(product) // Recherche les entités
                .stream() // Flux de conversion
                .map(SaleResponseDTO::fromEntity) // Conversion en DTO
                .collect(Collectors.toList()); // Liste finale
    }

    /**
     * Recherche avancée avec filtres dynamiques et pagination.
     *
     * Les différents paramètres permettent de filtrer les ventes
     * selon plusieurs critères :
     *
     * - nom du produit
     * - stock inférieur à une valeur
     * - plage de stock
     * - plage de prix
     * - plage de dates
     * - mot clé
     *
     * Les résultats sont paginés afin d'éviter de charger
     * une grande quantité de données en mémoire.
     *
     * Exemple d'appel API possible :
     *
     * /sales?page=0&size=10&product=laptop&minPrice=100&maxPrice=500
     */
    public PagedResponse<SaleResponseDTO> searchSales( // Retourne une page de DTOs
                                                       String product, // Filtre produit
                                                       Integer stockLessThan, // Filtre stock inférieur
                                                       Integer minStock, // Stock minimum
                                                       Integer maxStock, // Stock maximum
                                                       Double minPrice, // Prix minimum
                                                       Double maxPrice, // Prix maximum
                                                       LocalDateTime startDate, // Date début
                                                       LocalDateTime endDate, // Date fin
                                                       String keyword, // Mot-clé
                                                       Pageable pageable // Pagination
    ) {
        Specification<SaleEntity> spec = // Construction des filtres
                Specification.where(SaleSpecification.hasProduct(product)) // Filtre produit
                        .and(SaleSpecification.stockLessThan(stockLessThan)) // Filtre stock inférieur
                        .and(SaleSpecification.stockBetween(minStock, maxStock)) // Filtre plage stock
                        .and(SaleSpecification.priceBetween(minPrice, maxPrice)) // Filtre plage prix
                        .and(SaleSpecification.dateBetween(startDate, endDate)) // Filtre plage dates
                        .and(SaleSpecification.keywordSearch(keyword)); // Filtre mot-clé

        Page<SaleEntity> pageResult = saleRepository.findAll(spec, pageable); // Exécution de la requête

        List<SaleResponseDTO> content = pageResult.getContent() // Prend le contenu de la page
                .stream() // Flux
                .map(SaleResponseDTO::fromEntity) // Conversion en DTO
                .collect(Collectors.toList()); // Liste finale

        return new PagedResponse<>( // Construit la réponse paginée
                content, // Contenu converti
                pageResult.getNumber(), // Numéro de page
                pageResult.getSize(), // Taille de page
                pageResult.getTotalElements(), // Total éléments
                pageResult.getTotalPages() // Total pages
        );
    }

    /**
     * Récupère les ventes appartenant à un utilisateur.
     */
    public List<SaleResponseDTO> getSalesByUser(String email) { // Retourne des DTOs
        return saleRepository.findByUserEmail(email) // Recherche les entités
                .stream() // Flux
                .map(SaleResponseDTO::fromEntity) // Conversion en DTO
                .collect(Collectors.toList()); // Liste finale
    }

    /**
     * Récupère les ventes appartenant à un utilisateur avec pagination.
     */
    public PagedResponse<SaleResponseDTO> getPagedSalesByUser(String email, Pageable pageable) { // Retourne des DTOs paginés
        Page<SaleEntity> pageResult = saleRepository.findByUserEmail(email, pageable); // Récupère une page d'entités

        List<SaleResponseDTO> content = pageResult.getContent() // Lit le contenu
                .stream() // Flux
                .map(SaleResponseDTO::fromEntity) // Conversion en DTO
                .collect(Collectors.toList()); // Liste finale

        return new PagedResponse<>( // Construit la réponse paginée
                content, // Contenu DTO
                pageResult.getNumber(), // Numéro de page
                pageResult.getSize(), // Taille de page
                pageResult.getTotalElements(), // Total éléments
                pageResult.getTotalPages() // Total pages
        );
    }
}