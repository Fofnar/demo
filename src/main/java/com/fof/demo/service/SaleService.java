package com.fof.demo.service;

import com.fof.demo.dto.PagedResponse;
import com.fof.demo.entity.SaleEntity;
import com.fof.demo.repository.SaleRepository;
import com.fof.demo.specification.SaleSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;

    /**
     * Création d'une nouvelle vente.
     *
     * Les informations reçues sont utilisées pour construire
     * une nouvelle entité SaleEntity qui sera persistée
     * dans la base de données.
     */
    public SaleEntity createSale(LocalDateTime saleDate, String product, Double price, int quantity, int stock){

        SaleEntity saleEntity = new SaleEntity();

        saleEntity.setSaleDate(saleDate);
        saleEntity.setProduct(product);
        saleEntity.setPrice(price);
        saleEntity.setQuantity(quantity);
        saleEntity.setStock(stock);

        return saleRepository.save(saleEntity);
    }

    /**
     * Retourne toutes les ventes sans pagination.
     *
     * ⚠️ Utiliser principalement pour des tests ou
     * de petites quantités de données.
     */
    public List<SaleEntity> findAll(){
        return saleRepository.findAll();
    }

    /**
     * Recherche une vente par son identifiant.
     *
     * Si aucune vente n'est trouvée,
     * une exception est levée.
     */
    public SaleEntity findById(Long id){
        return saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found"));
    }

    /**
     * Suppression d'une vente par son identifiant.
     */
    public void deleteById(Long id){
        saleRepository.deleteById(id);
    }

    /**
     * Recherche des ventes après une certaine date.
     *
     * Exemple SQL :
     * SELECT * FROM sales WHERE sale_date > ?
     */
    public List<SaleEntity> findSalesAfter(LocalDateTime date){
        return saleRepository.findBySaleDateAfter(date);
    }

    /**
     * Recherche toutes les ventes d'un produit spécifique.
     *
     * Exemple SQL :
     * SELECT * FROM sales WHERE product = ?
     */
    public List<SaleEntity> findSalesByProduct(String product){
        return saleRepository.findByProduct(product);
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
     * -mot clé
     *
     * Les résultats sont paginés afin d'éviter de charger
     * une grande quantité de données en mémoire.
     *
     * Exemple d'appel API possible :
     *
     * /sales?page=0&size=10&product=laptop&minPrice=100&maxPrice=500
     */
    public PagedResponse<SaleEntity> searchSales(
            String product,
            Integer stockLessThan,
            Integer minStock,
            Integer maxStock,
            Double minPrice,
            Double maxPrice,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String keyword,
            Pageable pageable
    ){


        /**
         * Construction dynamique des filtres via Specification.
         *
         * Chaque filtre est combiné avec AND.
         * Si un paramètre est null, la Specification correspondante
         * retourne null et sera ignorée automatiquement par Spring.
         */
        Specification<SaleEntity> spec =
                Specification.where(SaleSpecification.hasProduct(product))
                        .and(SaleSpecification.stockLessThan(stockLessThan))
                        .and(SaleSpecification.stockBetween(minStock, maxStock))
                        .and(SaleSpecification.priceBetween(minPrice, maxPrice))
                        .and(SaleSpecification.dateBetween(startDate, endDate))
                        .and(SaleSpecification.keywordSearch(keyword));

        /**
         * Exécution de la requête en base de données avec
         * les filtres et la pagination.
         */
        Page<SaleEntity> pageResult = saleRepository.findAll(spec, pageable);

        /**
         * Construction de la réponse paginée personnalisée
         * utilisée par l'API.
         */
        return new PagedResponse<>(
                pageResult.getContent(),
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }
}