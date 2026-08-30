package com.example.supplierdeliveryapp.dto;

import java.math.BigDecimal;

public record DeliveryItemResponse(
        Long productId,
        String productName,
        BigDecimal weight,
        BigDecimal price,
        BigDecimal cost
) {
}