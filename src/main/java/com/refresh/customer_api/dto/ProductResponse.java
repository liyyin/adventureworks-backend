package com.refresh.customer_api.dto;

import com.refresh.customer_api.entity.Product;

import java.math.BigDecimal;

public record ProductResponse(
        Integer id,
        String name,
        String productNumber,
        String color,
        BigDecimal listPrice
) {
    public static ProductResponse from(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getProductNumber(),
                p.getColor(), p.getListPrice());
    }
}