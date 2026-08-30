package com.example.supplierdeliveryapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record DeliveryItemRequest(
        @NotNull(message = "ID товара обязателен")
        Long productId,

        @NotNull(message = "Вес обязателен")
        @Positive(message = "Вес должен быть больше нуля")
        BigDecimal weight
) {
}