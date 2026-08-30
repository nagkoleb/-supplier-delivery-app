package com.example.supplierdeliveryapp.repository;

import com.example.supplierdeliveryapp.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository
        extends JpaRepository<Supplier, Long> {
    boolean existsByName(String name);
}
