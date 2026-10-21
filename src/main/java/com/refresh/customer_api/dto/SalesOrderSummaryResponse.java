package com.refresh.customer_api.dto;

import com.refresh.customer_api.entity.SalesOrderHeader;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SalesOrderSummaryResponse(
        Integer id,
        String salesOrderNumber,
        LocalDateTime orderDate,
        Integer status,
        Integer customerId,
        BigDecimal totalDue
) {
    public static SalesOrderSummaryResponse from(SalesOrderHeader h) {
        return new SalesOrderSummaryResponse(
                h.getId(),
                h.getSalesOrderNumber(),
                h.getOrderDate(),
                h.getStatus(),
                h.getCustomerId(),
                h.getTotalDue());
    }
}