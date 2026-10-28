package com.refresh.customer_api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SalesOrderItemRequest(
        @NotNull(message = "Product wajib diisi")
        Integer productId,

        @NotNull(message = "Jumlah wajib diisi")
        @Min(value = 1, message = "Jumlah minimal 1")
        @Max(value = 32767, message = "Jumlah maksimal 32767")
        Integer orderQty
) {
}
