package com.fof.demo.controller;

import com.fof.demo.dto.ApiResponse; // Réponse API standardisée
import com.fof.demo.dto.PagedResponse; // Réponse paginée standardisée
import com.fof.demo.dto.SaleResponseDTO; // DTO de réponse des ventes
import com.fof.demo.dto.AISaleDTO; // DTO d'entrée pour la création
import com.fof.demo.entity.AppUser; // Entité utilisateur
import com.fof.demo.service.AppUserService; // Service des utilisateurs
import com.fof.demo.service.SaleService; // Service des ventes
import jakarta.validation.Valid; // Validation des entrées
import lombok.RequiredArgsConstructor; // Génère le constructeur des dépendances

import org.springdoc.core.annotations.ParameterObject; // Permet documenter la pagination Spring
import org.springframework.data.domain.Pageable; // Paramètres page/size/sort
import org.springframework.data.web.PageableDefault; // Valeurs par défaut de pagination
import org.springframework.format.annotation.DateTimeFormat; // Convertit les dates query params
import org.springframework.http.ResponseEntity; // Représente la réponse HTTP
import org.springframework.security.access.prepost.PreAuthorize; // Sécurise les endpoints
import org.springframework.security.core.Authentication; // Contient l'utilisateur connecté
import org.springframework.web.bind.annotation.*; // Annotations REST

import java.time.LocalDateTime; // Type date/heure backend
import java.util.List; // Liste de DTOs

import io.swagger.v3.oas.annotations.Operation; // Swagger : description des endpoints
import io.swagger.v3.oas.annotations.Parameter; // Swagger : description des paramètres
import io.swagger.v3.oas.annotations.media.Content; // Swagger : contenu de réponse
import io.swagger.v3.oas.annotations.media.ExampleObject; // Swagger : exemple JSON
import io.swagger.v3.oas.annotations.media.Schema; // Swagger : schéma des objets
import io.swagger.v3.oas.annotations.responses.ApiResponses; // Swagger : réponses possibles

/**
 * Contrôleur REST dédié à la gestion des ventes.
 *
 * Ce contrôleur expose des endpoints permettant de :
 * - créer une vente
 * - rechercher les ventes avec filtres et pagination
 * - récupérer une vente par identifiant
 * - supprimer une vente
 * - rechercher des ventes par produit
 * - rechercher des ventes après une date donnée
 *
 * Toutes les routes sont préfixées par :
 *      /api/sales
 *
 * Les réponses sont structurées via ApiResponse afin de garder
 * une cohérence dans toute l'API.
 */
@RestController // Déclare un controller REST
@RequestMapping("/api/sales") // Préfixe commun des routes
@RequiredArgsConstructor // Génère le constructeur des dépendances final
public class SaleController { // Controller des ventes

    private final SaleService saleService; // Service métier des ventes
    private final AppUserService userService; // Service métier des utilisateurs

    /**
     * Crée une nouvelle vente.
     *
     * L'objet reçu contient :
     * - la date de vente
     * - le nom du produit
     * - le prix
     * - la quantité
     * - le stock restant
     * - le user, identifié à partir de son email contenu dans le JWT
     *
     * @param saleDTO données de la vente à enregistrer
     * @return la vente créée encapsulée dans une ApiResponse
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')") // Autorise USER et ADMIN
    @PostMapping // Route POST /api/sales
    @Operation(
            summary = "Create a new sale", // Résumé Swagger
            description = "Creates a sale record from the provided sale data." // Description Swagger
    )
    @ApiResponses(value = { // Réponses Swagger
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Sale created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody( // Documentation du body
            description = "Sale data to create a new sale record", // Description
            required = true, // Body requis
            content = @Content( // Contenu documenté
                    schema = @Schema(implementation = AISaleDTO.class), // Schéma JSON
                    examples = @ExampleObject( // Exemple JSON
                            name = "Valid sale example", // Nom d'exemple
                            value = """
                                    {
                                      "date": "2026-03-01T09:00:00",
                                      "product": "Laptop",
                                      "price": 1000,
                                      "quantity": 2,
                                      "stock": 50
                                    }
                                    """
                    )
            )
    )
    public ResponseEntity<ApiResponse<SaleResponseDTO>> createSale( // Retourne un DTO de réponse
                                                                    @RequestBody @Valid AISaleDTO saleDTO, // Body d'entrée validé
                                                                    Authentication authentication // Utilisateur connecté
    ) {
        String email = authentication.getName(); // Récupère l'email depuis le JWT
        AppUser user = userService.loadUserByUsername(email); // Récupère l'utilisateur en base

        SaleResponseDTO created = saleService.createSale( // Crée la vente et renvoie un DTO
                saleDTO.getDate(), // Date
                saleDTO.getProduct(), // Produit
                saleDTO.getPrice(), // Prix
                saleDTO.getQuantity(), // Quantité
                saleDTO.getStock(), // Stock
                user // Propriétaire
        );

        ApiResponse<SaleResponseDTO> response = new ApiResponse<>( // Construit la réponse API
                true, // Succès
                created, // Donnée de sortie
                "Sale created successfully", // Message
                LocalDateTime.now() // Timestamp
        );

        return ResponseEntity.ok(response); // Renvoie HTTP 200
    }

    /**
     * Recherche des ventes selon plusieurs critères.
     *
     * Les filtres disponibles permettent de :
     * - filtrer par produit
     * - filtrer par stock minimal ou maximal
     * - filtrer par prix minimal ou maximal
     * - filtrer par date de début et de fin
     * - filtrer par mot-clé
     * - paginer et trier les résultats
     *
     * @param product filtre sur le nom du produit
     * @param stockLessThan filtre les ventes dont le stock est inférieur à cette valeur
     * @param minStock stock minimum autorisé
     * @param maxStock stock maximum autorisé
     * @param minPrice prix minimum autorisé
     * @param maxPrice prix maximum autorisé
     * @param startDate date de début de la recherche
     * @param endDate date de fin de la recherche
     * @param keyword mot-clé global de recherche
     * @param pageable objet Spring pour pagination et tri
     * @return une réponse paginée contenant les ventes trouvées
     */
    @PreAuthorize("hasRole('ADMIN')") // Réservé aux administrateurs
    @GetMapping("/search") // Route GET /api/sales/search
    @Operation(
            summary = "Search sales with filters and pagination", // Résumé Swagger
            description = "Searches sales using multiple optional filters such as product, stock, price, date range, keyword, pagination and sorting." // Description Swagger
    )
    @ApiResponses(value = { // Réponses possibles Swagger
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Sales fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<PagedResponse<SaleResponseDTO>>> searchSales( // Retourne des DTOs paginés
                                                                                    @Parameter(description = "Product name filter", example = "Mouse") // Swagger param
                                                                                    @RequestParam(required = false) String product, // Filtre produit

                                                                                    @Parameter(description = "Maximum stock allowed (exclusive)", example = "10") // Swagger param
                                                                                    @RequestParam(required = false) Integer stockLessThan, // Filtre stock inférieur

                                                                                    @Parameter(description = "Minimum stock allowed", example = "1") // Swagger param
                                                                                    @RequestParam(required = false) Integer minStock, // Stock minimum

                                                                                    @Parameter(description = "Maximum stock allowed", example = "20") // Swagger param
                                                                                    @RequestParam(required = false) Integer maxStock, // Stock maximum

                                                                                    @Parameter(description = "Minimum price allowed", example = "10") // Swagger param
                                                                                    @RequestParam(required = false) Double minPrice, // Prix minimum

                                                                                    @Parameter(description = "Maximum price allowed", example = "900") // Swagger param
                                                                                    @RequestParam(required = false) Double maxPrice, // Prix maximum

                                                                                    @Parameter(description = "Start date-time filter in ISO format", example = "2026-03-01T00:00:00") // Swagger param
                                                                                    @RequestParam(required = false)
                                                                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate, // Date de début

                                                                                    @Parameter(description = "End date-time filter in ISO format", example = "2026-03-31T23:59:59") // Swagger param
                                                                                    @RequestParam(required = false)
                                                                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate, // Date de fin

                                                                                    @Parameter(description = "Global keyword search", example = "Lap") // Swagger param
                                                                                    @RequestParam(required = false) String keyword, // Mot-clé global

                                                                                    @ParameterObject // Swagger + SpringDoc pour la pagination
                                                                                    @PageableDefault(size = 10, sort = "saleDate") Pageable pageable // Pagination par défaut
    ) {
        PagedResponse<SaleResponseDTO> pagedResponse = saleService.searchSales( // Recherche et conversion en DTO
                product, // Produit
                stockLessThan, // Stock inférieur
                minStock, // Stock minimum
                maxStock, // Stock maximum
                minPrice, // Prix minimum
                maxPrice, // Prix maximum
                startDate, // Date début
                endDate, // Date fin
                keyword, // Mot-clé
                pageable // Pagination
        );

        ApiResponse<PagedResponse<SaleResponseDTO>> response = new ApiResponse<>( // Réponse standardisée
                true, // Succès
                pagedResponse, // Données paginées
                "Sales fetched", // Message
                LocalDateTime.now() // Timestamp
        );

        return ResponseEntity.ok(response); // Renvoie HTTP 200
    }

    /**
     * Récupère une vente par son identifiant.
     *
     * @param id identifiant de la vente
     * @return la vente correspondante
     */
    @PreAuthorize("hasRole('ADMIN')") // Réservé aux administrateurs
    @GetMapping("/{id}") // Route GET /api/sales/{id}
    @Operation(
            summary = "Get sale by id", // Résumé Swagger
            description = "Retrieves a sale using its unique identifier." // Description Swagger
    )
    @ApiResponses(value = { // Réponses Swagger
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Sale found successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid sale id"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Sale not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<SaleResponseDTO>> getById( // Retourne un DTO
                                                                 @Parameter(description = "Sale identifier", example = "1", required = true) // Swagger param
                                                                 @PathVariable @Valid Long id // Identifiant
    ) {
        SaleResponseDTO sale = saleService.findById(id); // Récupère la vente en DTO

        ApiResponse<SaleResponseDTO> response = new ApiResponse<>( // Réponse standardisée
                true, // Succès
                sale, // Données
                "Sale found", // Message
                LocalDateTime.now() // Timestamp
        );

        return ResponseEntity.ok(response); // Renvoie HTTP 200
    }

    /**
     * Supprime une vente par son identifiant.
     *
     * @param id identifiant de la vente à supprimer
     * @return une réponse confirmant la suppression
     */
    @PreAuthorize("hasRole('ADMIN')") // Réservé aux administrateurs
    @DeleteMapping("/{id}") // Route DELETE /api/sales/{id}
    @Operation(
            summary = "Delete sale by id", // Résumé Swagger
            description = "Deletes a sale using its unique identifier." // Description Swagger
    )
    @ApiResponses(value = { // Réponses Swagger
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Sale deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid sale id"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Sale not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<Void>> deleteById( // Supprime sans renvoyer de contenu utile
                                                         @Parameter(description = "Sale identifier", example = "1", required = true) // Swagger param
                                                         @PathVariable @Valid Long id // Identifiant
    ) {
        saleService.deleteById(id); // Supprime la vente

        ApiResponse<Void> response = new ApiResponse<>( // Réponse standardisée
                true, // Succès
                null, // Aucune donnée
                "Sale deleted successfully", // Message
                LocalDateTime.now() // Timestamp
        );

        return ResponseEntity.ok(response); // Renvoie HTTP 200
    }

    /**
     * Récupère toutes les ventes d'un produit donné.
     *
     * @param product nom du produit recherché
     * @return liste des ventes correspondant au produit
     */
    @PreAuthorize("hasRole('ADMIN')") // Réservé aux administrateurs
    @GetMapping("/by-product") // Route GET /api/sales/by-product
    @Operation(
            summary = "Get sales by product", // Résumé Swagger
            description = "Retrieves all sales for a given product name." // Description Swagger
    )
    @ApiResponses(value = { // Réponses Swagger
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product sales found successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid product parameter"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<List<SaleResponseDTO>>> findSalesByProduct( // Retourne des DTOs
                                                                                  @Parameter(description = "Product name", example = "Mouse", required = true) // Swagger param
                                                                                  @RequestParam String product // Nom du produit
    ) {
        List<SaleResponseDTO> saleList = saleService.findSalesByProduct(product); // Récupère les DTOs

        return ResponseEntity.ok( // Renvoie une réponse 200
                new ApiResponse<>( // Réponse standardisée
                        true, // Succès
                        saleList, // Liste de DTOs
                        "Product successfully found", // Message
                        LocalDateTime.now() // Timestamp
                )
        );
    }

    /**
     * Récupère toutes les ventes effectuées après une date donnée.
     *
     * @param date date de référence
     * @return liste des ventes après cette date
     */
    @PreAuthorize("hasRole('ADMIN')") // Réservé aux administrateurs
    @GetMapping("/after") // Route GET /api/sales/after
    @Operation(
            summary = "Get sales after a given date", // Résumé Swagger
            description = "Retrieves all sales created after the specified date-time." // Description Swagger
    )
    @ApiResponses(value = { // Réponses Swagger
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Sales found successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid date parameter"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<List<SaleResponseDTO>>> findSaleAfter( // Retourne des DTOs
                                                                             @Parameter(description = "Reference date-time in ISO format", example = "2026-03-05T00:00:00", required = true) // Swagger param
                                                                             @RequestParam // Paramètre query
                                                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date // Date recherchée
    ) {
        List<SaleResponseDTO> saleList = saleService.findSalesAfter(date); // Récupère les DTOs

        return ResponseEntity.ok( // Renvoie une réponse 200
                new ApiResponse<>( // Réponse standardisée
                        true, // Succès
                        saleList, // Liste de DTOs
                        "Product successfully found", // Message
                        LocalDateTime.now() // Timestamp
                )
        );
    }

    /**
     * Récupère les ventes appartenant à l'utilisateur authentifié.
     *
     * Le user est identifié à partir de son email contenu dans le JWT.
     * Les ventes retournées sont donc limitées à celles liées à cet utilisateur.
     *
     * @param authentication objet Spring Security contenant les informations du user connecté
     * @return liste des ventes de l'utilisateur connecté encapsulée dans ApiResponse
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')") // Autorise USER et ADMIN
    @GetMapping("/my-sales") // Route GET /api/sales/my-sales
    @Operation(
            summary = "Get current user's sales", // Résumé Swagger
            description = "Retrieves only the sales created by the authenticated user." // Description Swagger
    )
    @ApiResponses(value = { // Réponses Swagger
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "User sales fetched successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<ApiResponse<List<SaleResponseDTO>>> getMySales(Authentication authentication) { // Retourne des DTOs

        String email = authentication.getName(); // Récupère l'email du user connecté
        List<SaleResponseDTO> sales = saleService.getSalesByUser(email); // Récupère les DTOs

        return ResponseEntity.ok( // Renvoie une réponse 200
                new ApiResponse<>(true, sales, "User sales fetched successfully", LocalDateTime.now()) // Réponse standardisée
        );
    }

    /**
     * Récupère les ventes paginées appartenant à l'utilisateur authentifié.
     *
     * Le user est identifié à partir de son email contenu dans le JWT.
     * Les ventes retournées sont donc limitées à celles liées à cet utilisateur,
     * avec pagination et tri.
     *
     * Cette route est accessible aux rôles USER et ADMIN.
     * Cela permet à un administrateur de consulter aussi ses propres ventes
     *
     * URL : GET /api/sales/my-sales/paged
     *
     * @param authentication objet Spring Security contenant les informations du user connecté
     * @param pageable objet Spring pour pagination et tri
     * @return réponse paginée contenant les ventes du user connecté
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')") // Autorise USER et ADMIN
    @GetMapping("/my-sales/paged") // Route GET /api/sales/my-sales/paged
    @Operation(
            summary = "Get current user's paged sales", // Résumé Swagger
            description = "Retrieves only the sales created by the authenticated user, with pagination and sorting." // Description Swagger
    )
    @ApiResponses(value = { // Réponses Swagger
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Paged user sales fetched successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<ApiResponse<PagedResponse<SaleResponseDTO>>> getPagedMySales( // Retourne des DTOs paginés
                                                                                        @ParameterObject // Swagger + SpringDoc
                                                                                        @PageableDefault(size = 10, sort = "saleDate") Pageable pageable, // Pagination
                                                                                        Authentication authentication // Utilisateur connecté
    ) {
        String email = authentication.getName(); // Récupère l'email du user connecté

        PagedResponse<SaleResponseDTO> pagedSales = saleService.getPagedSalesByUser(email, pageable); // Récupère les DTOs paginés

        ApiResponse<PagedResponse<SaleResponseDTO>> response = new ApiResponse<>( // Réponse standardisée
                true, // Succès
                pagedSales, // Données paginées
                "User sales fetched successfully", // Message
                LocalDateTime.now() // Timestamp
        );

        return ResponseEntity.ok(response); // Renvoie HTTP 200
    }
}