package com.example.supplierdeliveryapp.dto;

import com.example.supplierdeliveryapp.entity.ProductType;

public record ProductResponse(
        Long id,
        String name,
        ProductType type
) {
}