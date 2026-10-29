package com.refresh.customer_api.dto;

import com.refresh.customer_api.entity.Customer;
import com.refresh.customer_api.entity.Person;
import com.refresh.customer_api.entity.SalesOrderHeader;
import com.refresh.customer_api.entity.Store;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record SalesOrderDetailResponse(
        Integer id,
        String salesOrderNumber,
        LocalDateTime orderDate,
        LocalDateTime dueDate,
        LocalDateTime shipDate,
        Integer status,
        String statusLabel,
        Integer customerId,
        String personName,
        String storeName,
        BigDecimal subTotal,
        BigDecimal taxAmt,
        BigDecimal freight,
        BigDecimal totalDue,
        List<SalesOrderItemResponse> items,
        List<String> salesReasons
) {
    public static SalesOrderDetailResponse from(SalesOrderHeader h,List<String> salesReasons) {
        Customer customer = h.getCustomer();
        Person person = customer != null ? customer.getPerson() : null;
        Store store = customer != null ? customer.getStore() : null;
        return new SalesOrderDetailResponse(
                h.getId(),
                h.getSalesOrderNumber(),
                h.getOrderDate(),
                h.getDueDate(),
                h.getShipDate(),
                h.getStatus(),
                h.getStatusEnum().getLabel(),
                h.getCustomerId(),
                person != null
                        ? joinName(person.getFirstName(), person.getMiddleName(), person.getLastName())
                        : null,
                store != null ? store.getName() : null,
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

    private static String joinName(String ... parts){
        String result = Stream.of(parts)
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining(" "));
        return result.isEmpty() ? null:result;
    }
}
