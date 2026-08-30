package com.example.supplierdeliveryapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SupplierPriceResponse(
        Long id,
        Long supplierId,
        String supplierName,
        Long productId,
        String productName,
        BigDecimal price,
        LocalDate dateFrom,
        LocalDate dateTo
) {
}