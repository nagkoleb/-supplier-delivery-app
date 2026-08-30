package com.example.supplierdeliveryapp.controller;

import com.example.supplierdeliveryapp.dto.SupplierPriceRequest;
import com.example.supplierdeliveryapp.dto.SupplierPriceResponse;
import com.example.supplierdeliveryapp.entity.SupplierPrice;
import com.example.supplierdeliveryapp.service.SupplierPriceService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supplier-prices")
public class SupplierPriceController {
    private final SupplierPriceService supplierPriceService;

    public SupplierPriceController(SupplierPriceService supplierPriceService) {
        this.supplierPriceService = supplierPriceService;
    }

    @PostMapping
    public SupplierPriceResponse createSupplierPrice(
            @Valid @RequestBody SupplierPriceRequest request
    ) {
        SupplierPrice supplierPrice = supplierPriceService.createSupplierPrice(request);

        return new SupplierPriceResponse(
                supplierPrice.getId(),
                supplierPrice.getSupplier().getId(),
                supplierPrice.getSupplier().getName(),
                supplierPrice.getProduct().getId(),
                supplierPrice.getProduct().getName(),
                supplierPrice.getPrice(),
                supplierPrice.getDateFrom(),
                supplierPrice.getDateTo()
        );
    }

    @GetMapping
    public List<SupplierPriceResponse> getAllSupplierPrices() {
        return supplierPriceService.getAllSupplierPrices()
                .stream()
                .map(supplierPrice -> new SupplierPriceResponse(
                        supplierPrice.getId(),
                        supplierPrice.getSupplier().getId(),
                        supplierPrice.getSupplier().getName(),
                        supplierPrice.getProduct().getId(),
                        supplierPrice.getProduct().getName(),
                        supplierPrice.getPrice(),
                        supplierPrice.getDateFrom(),
                        supplierPrice.getDateTo()
                ))
                .toList();
    }
}
