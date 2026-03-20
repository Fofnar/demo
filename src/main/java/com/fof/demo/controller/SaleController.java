package com.fof.demo.controller;

import com.fof.demo.dto.AISaleDTO;
import com.fof.demo.dto.ApiResponse;
import com.fof.demo.dto.PagedResponse;
import com.fof.demo.entity.AppUser;
import com.fof.demo.entity.SaleEntity;
import com.fof.demo.service.AppUserService;
import com.fof.demo.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

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
@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;
    private final AppUserService userService;

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
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping
    @Operation(
            summary = "Create a new sale",
            description = "Creates a sale record from the provided sale data."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Sale created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Sale data to create a new sale record",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = AISaleDTO.class),
                    examples = @ExampleObject(
                            name = "Valid sale example",
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
    public ResponseEntity<ApiResponse<AISaleDTO>> createSale(
            @RequestBody @Valid AISaleDTO saleDTO,
            Authentication authentication
    ) {
        String email = authentication.getName();
        AppUser user = userService.loadUserByUsername(email);

        SaleEntity created = saleService.createSale(
                saleDTO.getDate(),
                saleDTO.getProduct(),
                saleDTO.getPrice(),
                saleDTO.getQuantity(),
                saleDTO.getStock(),
                user
        );

        AISaleDTO dto = new AISaleDTO(
                created.getSaleDate(),
                created.getProduct(),
                created.getPrice(),
                created.getQuantity(),
                created.getStock()
        );

        ApiResponse<AISaleDTO> response = new ApiResponse<>(
                true,
                dto,
                "Sale created successfully",
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
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
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search")
    @Operation(
            summary = "Search sales with filters and pagination",
            description = "Searches sales using multiple optional filters such as product, stock, price, date range, keyword, pagination and sorting."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Sales fetched successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<PagedResponse<SaleEntity>>> searchSales(
            @Parameter(description = "Product name filter", example = "Mouse")
            @RequestParam(required = false) String product,

            @Parameter(description = "Maximum stock allowed (exclusive)", example = "10")
            @RequestParam(required = false) Integer stockLessThan,

            @Parameter(description = "Minimum stock allowed", example = "1")
            @RequestParam(required = false) Integer minStock,

            @Parameter(description = "Maximum stock allowed", example = "20")
            @RequestParam(required = false) Integer maxStock,

            @Parameter(description = "Minimum price allowed", example = "10")
            @RequestParam(required = false) Double minPrice,

            @Parameter(description = "Maximum price allowed", example = "900")
            @RequestParam(required = false) Double maxPrice,

            @Parameter(description = "Start date-time filter in ISO format", example = "2026-03-01T00:00:00")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,

            @Parameter(description = "End date-time filter in ISO format", example = "2026-03-31T23:59:59")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,

            @Parameter(description = "Global keyword search", example = "Lap")
            @RequestParam(required = false) String keyword,

            @ParameterObject
            @PageableDefault(size = 10, sort = "saleDate") Pageable pageable
    ) {
        PagedResponse<SaleEntity> pagedResponse = saleService.searchSales(
                product,
                stockLessThan,
                minStock,
                maxStock,
                minPrice,
                maxPrice,
                startDate,
                endDate,
                keyword,
                pageable
        );

        ApiResponse<PagedResponse<SaleEntity>> response = new ApiResponse<>(
                true,
                pagedResponse,
                "Sales fetched",
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Récupère une vente par son identifiant.
     *
     * @param id identifiant de la vente
     * @return la vente correspondante
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    @Operation(
            summary = "Get sale by id",
            description = "Retrieves a sale using its unique identifier."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Sale found successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid sale id"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Sale not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<SaleEntity>> getById(
            @Parameter(description = "Sale identifier", example = "1", required = true)
            @PathVariable @Valid Long id
    ) {
        SaleEntity sale = saleService.findById(id);

        ApiResponse<SaleEntity> response = new ApiResponse<>(
                true,
                sale,
                "Sale found",
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Supprime une vente par son identifiant.
     *
     * @param id identifiant de la vente à supprimer
     * @return une réponse confirmant la suppression
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete sale by id",
            description = "Deletes a sale using its unique identifier."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Sale deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid sale id"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Sale not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<Void>> deleteById(
            @Parameter(description = "Sale identifier", example = "1", required = true)
            @PathVariable @Valid Long id
    ) {
        saleService.deleteById(id);

        ApiResponse<Void> response = new ApiResponse<>(
                true,
                null,
                "Sale deleted successfully",
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Récupère toutes les ventes d'un produit donné.
     *
     * @param product nom du produit recherché
     * @return liste des ventes correspondant au produit
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/by-product")
    @Operation(
            summary = "Get sales by product",
            description = "Retrieves all sales for a given product name."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product sales found successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid product parameter"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<List<SaleEntity>>> findSalesByProduct(
            @Parameter(description = "Product name", example = "Mouse", required = true)
            @RequestParam String product
    ) {
        List<SaleEntity> saleList = saleService.findSalesByProduct(product);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        saleList,
                        "Product successfully found",
                        LocalDateTime.now()
                )
        );
    }

    /**
     * Récupère toutes les ventes effectuées après une date donnée.
     *
     * @param date date de référence
     * @return liste des ventes après cette date
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/after")
    @Operation(
            summary = "Get sales after a given date",
            description = "Retrieves all sales created after the specified date-time."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Sales found successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid date parameter"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiResponse<List<SaleEntity>>> findSaleAfter(
            @Parameter(description = "Reference date-time in ISO format", example = "2026-03-05T00:00:00", required = true)
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date
    ) {
        List<SaleEntity> saleList = saleService.findSalesAfter(date);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        saleList,
                        "Product successfully found",
                        LocalDateTime.now()
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
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/my-sales")
    @Operation(
            summary = "Get current user's sales",
            description = "Retrieves only the sales created by the authenticated user."
    )
    @ApiResponses(value = {
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
    public ResponseEntity<ApiResponse<List<SaleEntity>>> getMySales(Authentication authentication) {

        String email = authentication.getName();
        List<SaleEntity> sales = saleService.getSalesByUser(email);

        return ResponseEntity.ok(
                new ApiResponse<>(true, sales, "User sales fetched successfully", LocalDateTime.now())
        );
    }
}