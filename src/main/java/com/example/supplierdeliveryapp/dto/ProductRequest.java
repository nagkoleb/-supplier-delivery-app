package com.example.supplierdeliveryapp.dto;

import com.example.supplierdeliveryapp.entity.ProductType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductRequest(
        @NotBlank(message = "Название товара не должно быть пустым")
        String name,

        @NotNull(message = "Тип товара обязателен")
        ProductType type
) {
}