package com.refresh.customer_api.controller;

import com.refresh.customer_api.dto.ProductResponse;
import com.refresh.customer_api.dto.ProductSelectorResponse;
import com.refresh.customer_api.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public Page<ProductResponse> list(@RequestParam(defaultValue = "") String name,
                                      Pageable pageable) {
        return productService.search(name, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> get(@PathVariable Integer id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/select")
    public List<ProductSelectorResponse> list(){
        return productService.getAllWithQuery();
    }

    @GetMapping("/select-projection-bynamejpa")
    public List<ProductSelectorResponse> selectorProjectionByJpa(){
        return productService.getAllWithProjectionByJpa();
    }
}