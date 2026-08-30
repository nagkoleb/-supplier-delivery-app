package com.example.supplierdeliveryapp.repository;

import com.example.supplierdeliveryapp.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    boolean existsBySupplier_Id(Long supplierId);


}
