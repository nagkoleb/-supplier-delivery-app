package com.example.supplierdeliveryapp.controller;

import com.example.supplierdeliveryapp.dto.SupplierRequest;
import com.example.supplierdeliveryapp.dto.SupplierResponse;
import com.example.supplierdeliveryapp.entity.Supplier;
import com.example.supplierdeliveryapp.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @PostMapping
    public SupplierResponse createSupplier(
            @Valid @RequestBody SupplierRequest request
    ) {
        Supplier supplier = supplierService.createSupplier(request);

        return new SupplierResponse(
                supplier.getId(),
                supplier.getName()
        );
    }

    @GetMapping
    public List<SupplierResponse> getAllSuppliers() {
        return supplierService.getAllSuppliers()
                .stream()
                .map(supplier -> new SupplierResponse(
                        supplier.getId(),
                        supplier.getName()
                ))
                .toList();
    }

    @PutMapping("/{id}")
    public SupplierResponse updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody SupplierRequest request
    ) {
        Supplier supplier = supplierService.updateSupplier(id, request);

        return new SupplierResponse(
                supplier.getId(),
                supplier.getName()
        );
    }

    @DeleteMapping("/{id}")
    public void deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
    }
}