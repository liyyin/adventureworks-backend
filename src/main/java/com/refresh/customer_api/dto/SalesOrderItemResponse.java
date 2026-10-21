package com.refresh.customer_api.dto;

import com.refresh.customer_api.entity.SalesOrderDetail;

import java.math.BigDecimal;

public record SalesOrderItemResponse(
        Integer id,
        Integer productId,
        String productName,
        Integer orderQty,
        BigDecimal unitPrice,
        BigDecimal unitPriceDiscount,
        BigDecimal lineTotal
) {
    public static SalesOrderItemResponse from(SalesOrderDetail d) {
        return new SalesOrderItemResponse(
                d.getId(),
                d.getProduct().getId(),
                d.getProduct().getName(),
                d.getOrderQty(),
                d.getUnitPrice(),
                d.getUnitPriceDiscount(),
                d.getLineTotal());
    }
}