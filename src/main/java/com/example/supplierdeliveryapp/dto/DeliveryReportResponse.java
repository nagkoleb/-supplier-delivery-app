package com.example.supplierdeliveryapp.dto;

import java.math.BigDecimal;

public record DeliveryReportResponse(
        Long supplierId,
        String supplierName,
        Long productId,
        String productName,
        BigDecimal totalWeight,
        BigDecimal totalCost
) {
}