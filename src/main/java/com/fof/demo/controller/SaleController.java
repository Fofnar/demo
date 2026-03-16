package com.fof.demo.controller;

import com.fof.demo.dto.AISaleDTO;
import com.fof.demo.dto.ApiResponse;
import com.fof.demo.dto.PagedResponse;
import com.fof.demo.entity.SaleEntity;
import com.fof.demo.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    public ResponseEntity<ApiResponse<AISaleDTO>> createSale(@RequestBody @Valid AISaleDTO saleDTO){
        SaleEntity created = saleService.createSale(
                saleDTO.getDate(),
                saleDTO.getProduct(),
                saleDTO.getPrice(),
                saleDTO.getQuantity(),
                saleDTO.getStock()
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

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PagedResponse<SaleEntity>>> searchSales(
            @RequestParam(required = false) String product,
            @RequestParam(required = false) Integer stockLessThan,
            @RequestParam(required = false) Integer minStock,
            @RequestParam(required = false) Integer maxStock,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "saleDate")Pageable pageable
            ){

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

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SaleEntity>> getById(@PathVariable @Valid Long id){

        SaleEntity sale = saleService.findById(id);

        ApiResponse<SaleEntity> response = new ApiResponse<>(
                true,
                sale,
                "Sale found",
                LocalDateTime.now()
        );
        return ResponseEntity.ok(response);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteById(@PathVariable @Valid Long id){
        saleService.deleteById(id);
        ApiResponse<Void> response = new ApiResponse<>(
                true,
                null,
                "Sale deleted successfully",
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-product")
    public ResponseEntity<ApiResponse<List<SaleEntity>>> findSalesByProduct(@RequestParam String product){
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

    @GetMapping("/after")
    public ResponseEntity<ApiResponse<List<SaleEntity>>> findSaleAfter(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date
    ){
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

}
