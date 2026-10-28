package com.refresh.customer_api.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductRequest(
        @NotBlank(message = "Nama Produk Wajib Diisi")
        @Size(max = 50, message = "Nama Produk maksimal 50 karakter")
        String name,

        @NotBlank(message = "Nomor Produk wajib diisi")
        @Size(max = 25,message = "nomor produk maksimal 25 karakter")
        String productNumber,

        @Size(max = 15, message = "Warna maksimal 15 karakter")
        String color,

        @NotNull(message = "standard cost wajib diisi")
        @DecimalMin(value = "0.0", message = "standard cost tidak bisa negatif")
        BigDecimal standardCost,

        @NotNull(message = "harga tidak boleh kosong")
        @DecimalMin(value = "0.0", message = "harga tidak dapat negatif")
        BigDecimal listPrice,

        @NotNull(message = "safety stock level wajib diisi")
        @Max(value = 32767, message = "safety stock level maksimal 32767")
        @Min(value = 1,message = "safety stock level minimal 1")
        Integer safetyStockLevel,

        @NotNull(message = "reorder point wajib diisi")
        @Max(value = 32767,message = "reorder point maksimal 32767")
        @Min(value = 1,message = "reorder point minimal 1")
        Integer reorderPoint,

        @NotNull(message = "day to manufacture wajib diisi")
        @Min(value = 0,message = "day to manufacture tidak dapat negatif")
        Integer daysToManufacture,

        @NotNull(message = "tanggal mulai jual wajib diisi")
        LocalDateTime sellStartDate,

        LocalDateTime sellEndDate

) {
}
