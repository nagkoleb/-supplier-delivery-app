package com.example.supplierdeliveryapp.repository;

import com.example.supplierdeliveryapp.dto.DeliveryReportResponse;
import com.example.supplierdeliveryapp.entity.DeliveryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DeliveryItemRepository extends JpaRepository<DeliveryItem, Long> {

    @Query("""
            SELECT new com.example.supplierdeliveryapp.dto.DeliveryReportResponse(
                di.delivery.supplier.id,
                di.delivery.supplier.name,
                di.product.id,
                di.product.name,
                SUM(di.weight),
                SUM(di.cost)
            )
            FROM DeliveryItem di
            WHERE di.delivery.deliveryDate BETWEEN :dateFrom AND :dateTo
            GROUP BY
                di.delivery.supplier.id,
                di.delivery.supplier.name,
                di.product.id,
                di.product.name
            ORDER BY
                di.delivery.supplier.name,
                di.product.name
            """)
    List<DeliveryReportResponse> findReportByPeriod(
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo
    );

    boolean existsByProduct_Id(Long productId);
}