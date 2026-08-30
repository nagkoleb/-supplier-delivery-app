package com.example.supplierdeliveryapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SupplierRequest(
        @NotBlank(message = "Имя поставщика не должно быть пустым")
        @Size(
                min = 2,
                max = 255,
                message = "Имя поставщика должно содержать от 2 до 255 символов"
        )
        String name
) {
}