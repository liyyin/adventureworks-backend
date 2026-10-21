package com.refresh.customer_api.controller;

import com.refresh.customer_api.dto.SalesOrderDetailResponse;
import com.refresh.customer_api.dto.SalesOrderSummaryResponse;
import com.refresh.customer_api.service.SalesOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Sales Orders", description = "Transaksi penjualan AdventureWorks")
@RestController
@RequestMapping ("/sales-orders")
@RequiredArgsConstructor
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    @Operation(summary = "Daftar sales order dengan paging")
    @GetMapping
    public Page<SalesOrderSummaryResponse> list(@ParameterObject Pageable pageable) {
        return salesOrderService.list(pageable);
    }

    @Operation(summary = "Detail sales order beserta item-itemnya")
    @GetMapping("/{id}")
    public SalesOrderDetailResponse get(@PathVariable Integer id) {
        return salesOrderService.getById(id);
    }
}
