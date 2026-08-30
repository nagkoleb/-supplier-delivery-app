package com.example.supplierdeliveryapp.dto;

import java.time.LocalDate;
import java.util.List;

public record DeliveryResponse(
        Long id,
        Long supplierId,
        String supplierName,
        LocalDate deliveryDate,
        List<DeliveryItemResponse> items
) {
}