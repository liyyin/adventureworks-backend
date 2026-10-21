package com.refresh.customer_api.controller;

import com.refresh.customer_api.dto.ProductResponse;
import com.refresh.customer_api.dto.ProductSelectorResponse;
import com.refresh.customer_api.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Products",description = "Data Produk AdventureWorks")
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "List produk dengan paging")
    @GetMapping
    public Page<ProductResponse> list(@RequestParam(defaultValue = "") String name,
                                      Pageable pageable) {
        return productService.search(name, pageable);
    }

    @Operation(summary = "List produk dengan manual query dengan paging")
    @GetMapping("/get-list")
    public Page<ProductResponse> listManual(@RequestParam(defaultValue = "") String keyword,
                                      @ParameterObject Pageable pageable) {
        return productService.searchManualQuery(keyword, pageable);
    }

    @Operation(summary = "Ambil detail produk berdasarkan ID")
    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable Integer id) {
        return productService.findById(id);
    }
    @Operation(summary = "Ini coba ambil list untuk select dipake di depan ambil semuanya tanpa pagination")
    @GetMapping("/select")
    public List<ProductSelectorResponse> list(){
        return productService.getAllWithQuery();
    }

    @Operation(summary = "Ini coba ambil list menggunakan projection dan name dari JPA langsung")
    @GetMapping("/select-projection-bynamejpa")
    public List<ProductSelectorResponse> selectorProjectionByJpa(){
        return productService.getAllWithProjectionByJpa();
    }
}