package com.refresh.customer_api.dto;

import com.refresh.customer_api.repository.projection.SalesOrderSummaryView;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SalesOrderWithCustomerResponse(
        Integer id,
        String salesOrderNumber,
        LocalDateTime orderDate,
        Integer status,
        Integer customerId,
        String personName,
        String storeName,
        BigDecimal totalDue
) {
    public static SalesOrderWithCustomerResponse from(SalesOrderSummaryView v){
        return new SalesOrderWithCustomerResponse(
                v.getId(),
                v.getSalesOrderNumber(),
                v.getOrderDate(),
                v.getStatus(),
                v.getCustomerId(),
                v.getPersonName(),
                v.getStoreName(),
                v.getTotalDue()
        );
    }
}
