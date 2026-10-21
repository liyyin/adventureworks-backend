package com.refresh.customer_api.dto;

import com.refresh.customer_api.entity.SalesOrderHeader;
import com.refresh.customer_api.entity.SalesReason;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SalesOrderDetailResponse(
        Integer id,
        String salesOrderNumber,
        LocalDateTime orderDate,
        LocalDateTime dueDate,
        LocalDateTime shipDate,
        Integer status,
        Integer customerId,
        BigDecimal subTotal,
        BigDecimal taxAmt,
        BigDecimal freight,
        BigDecimal totalDue,
        List<SalesOrderItemResponse> items,
        List<String> salesReasons
) {
    public static SalesOrderDetailResponse from(SalesOrderHeader h,List<String> salesReasons) {
        return new SalesOrderDetailResponse(
                h.getId(),
                h.getSalesOrderNumber(),
                h.getOrderDate(),
                h.getDueDate(),
                h.getShipDate(),
                h.getStatus(),
                h.getCustomerId(),
                h.getSubTotal(),
                h.getTaxAmt(),
                h.getFreight(),
                h.getTotalDue(),
                h.getDetails().stream()
                        .map(SalesOrderItemResponse::from)
                        .toList(),
                salesReasons
        );
    }
}
