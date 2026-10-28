package com.refresh.customer_api.controller;

import com.refresh.customer_api.dto.SalesOrderCreateRequest;
import com.refresh.customer_api.dto.SalesOrderDetailResponse;
import com.refresh.customer_api.dto.SalesOrderSummaryResponse;
import com.refresh.customer_api.dto.SalesOrderWithCustomerResponse;
import com.refresh.customer_api.service.SalesOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Sales Orders", description = "Transaksi penjualan AdventureWorks")
@RestController
@RequestMapping("/sales-orders")
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

    @Operation(summary = "Detail Sales order meminimaliskan berapa kali select")
    @GetMapping("/detail")
    public SalesOrderDetailResponse getDetailByJoinFetch(@RequestParam("id") Integer id) {
        return salesOrderService.getDetailById(id);
    }

    @Operation(summary = "Daftar Sales order menggunakan query native dan ambil person name dan store name dari customer ID")
    @GetMapping("/list-customer")
    public Page<SalesOrderWithCustomerResponse> listCustomer(@ParameterObject Pageable pageable) {
        return salesOrderService.listCustomerStore(pageable);
    }

    @Operation(summary = "get list customer menggunakan JPQL")
    @GetMapping("/list-customer-jpql")
    public Page<SalesOrderWithCustomerResponse> listCustomerJpql(@ParameterObject Pageable pageable) {
        return salesOrderService.listCustomerJpql(pageable);
    }

    @Operation(summary = "Add Sales order baru")
    @PostMapping
    public ResponseEntity<SalesOrderDetailResponse> create(@Valid @RequestBody SalesOrderCreateRequest request) {
        SalesOrderDetailResponse created = salesOrderService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }
}
