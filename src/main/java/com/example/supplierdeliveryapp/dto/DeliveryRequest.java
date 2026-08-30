package com.example.supplierdeliveryapp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record DeliveryRequest(
        @NotNull(message = "ID поставщика обязателен")
        Long supplierId,

        @NotNull(message = "Дата поставки обязательна")
        LocalDate deliveryDate,

        @NotEmpty(message = "Поставка должна содержать хотя бы один товар")
        List<@Valid DeliveryItemRequest> items
) {
}