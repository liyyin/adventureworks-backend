package com.refresh.customer_api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record SalesOrderUpdateRequest(

        @NotNull(message = "Tanggal Jatuh Tempo wajib diisi")
        LocalDateTime dueDate,

        @NotEmpty(message = "Minimal harus 1 item")
        List<@Valid SalesOrderItemRequest> items
) {
}
