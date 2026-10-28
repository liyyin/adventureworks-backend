package com.refresh.customer_api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record SalesOrderCreateRequest(
        @NotNull(message = "Customer wajib diisi")
        Integer customerId,

        @NotNull(message = "Alamat tagihan wajib diisi")
        Integer billToAddressId,

        @NotNull(message = "Alamat pengiriman wajib diisi")
        Integer shipToAddressId,

        @NotNull(message = "Metode pengiriman wajib diisi")
        Integer shipMethodId,

        @NotNull(message = "Tanggal jatuh tempo wajib diisi")
        LocalDateTime dueDate,

        @NotEmpty(message = "Minimal harus ada 1 item")
        List<@Valid SalesOrderItemRequest> items,

        List<@NotNull(message = "Sales Reson tidak boleh kosong") Integer> salesReasonIds
) {
}
