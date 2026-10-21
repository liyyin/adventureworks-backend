package com.refresh.customer_api.dto;

import com.refresh.customer_api.entity.Product;

public record ProductSelectorResponse(
        Integer id,
        String name,
        String productNumber
) {
    public static ProductSelectorResponse from(Product p){
        return new ProductSelectorResponse(p.getId(),p.getName(),p.getProductNumber());
    }
}
