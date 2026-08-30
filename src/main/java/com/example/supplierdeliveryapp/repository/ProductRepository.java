package com.example.supplierdeliveryapp.repository;

import com.example.supplierdeliveryapp.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String name);
}
