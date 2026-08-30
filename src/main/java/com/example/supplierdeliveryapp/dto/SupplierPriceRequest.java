package com.example.supplierdeliveryapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SupplierPriceRequest(
        @NotNull(message = "ID поставщика обязателен")
        Long supplierId,

        @NotNull(message = "ID товара обязателен")
        Long productId,

        @NotNull(message = "Цена обязательна")
        @Positive(message = "Цена должна быть больше нуля")
        BigDecimal price,

        @NotNull(message = "Дата начала действия цены обязательна")
        LocalDate dateFrom,

        LocalDate dateTo
) {
}