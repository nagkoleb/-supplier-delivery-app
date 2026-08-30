package com.example.supplierdeliveryapp.repository;

import com.example.supplierdeliveryapp.entity.SupplierPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface SupplierPriceRepository extends JpaRepository<SupplierPrice, Long> {
    @Query("""
                SELECT sp
                FROM SupplierPrice sp
                WHERE sp.supplier.id = :supplierId
                  AND sp.product.id = :productId
                  AND sp.dateFrom <= :date
                  AND (sp.dateTo >= :date OR sp.dateTo IS NULL)
            """)
    Optional<SupplierPrice> findActualPrice(
                                             @Param("supplierId") Long supplierId,
                                             @Param("productId") Long productId,
                                             @Param("date") LocalDate date
    );

    boolean existsBySupplier_IdAndProduct_IdAndDateFrom(
            Long supplierId,
            Long productId,
            LocalDate dateFrom
    );

    boolean existsBySupplier_IdAndProduct_IdAndDateFromAfter(
            Long supplierId,
            Long productId,
            LocalDate dateFrom
    );

    boolean existsBySupplier_Id(Long supplierId);
    boolean existsByProduct_Id(Long productId);
}

