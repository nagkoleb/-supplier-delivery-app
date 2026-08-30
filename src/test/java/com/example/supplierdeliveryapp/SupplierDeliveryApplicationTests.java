package com.example.supplierdeliveryapp;

import com.example.supplierdeliveryapp.dto.DeliveryItemRequest;
import com.example.supplierdeliveryapp.dto.DeliveryReportResponse;
import com.example.supplierdeliveryapp.dto.DeliveryRequest;
import com.example.supplierdeliveryapp.dto.DeliveryResponse;
import com.example.supplierdeliveryapp.dto.ProductRequest;
import com.example.supplierdeliveryapp.dto.SupplierPriceRequest;
import com.example.supplierdeliveryapp.dto.SupplierRequest;
import com.example.supplierdeliveryapp.entity.Product;
import com.example.supplierdeliveryapp.entity.ProductType;
import com.example.supplierdeliveryapp.entity.Supplier;
import com.example.supplierdeliveryapp.entity.SupplierPrice;
import com.example.supplierdeliveryapp.repository.ProductRepository;
import com.example.supplierdeliveryapp.repository.SupplierRepository;
import com.example.supplierdeliveryapp.service.DeliveryService;
import com.example.supplierdeliveryapp.service.ProductService;
import com.example.supplierdeliveryapp.service.ReportService;
import com.example.supplierdeliveryapp.service.SupplierPriceService;
import com.example.supplierdeliveryapp.service.SupplierService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SupplierDeliveryApplicationTests {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SupplierPriceService supplierPriceService;

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private ReportService reportService;

    @Test
    void contextLoads() {
    }

    @Test
    void shouldCreateSupplier() {
        Supplier supplier = supplierService.createSupplier(
                new SupplierRequest("Тестовый поставщик")
        );

        assertNotNull(supplier.getId());
        assertEquals(
                "Тестовый поставщик",
                supplier.getName()
        );
    }

    @Test
    void shouldCreateProduct() {
        Product product = productService.createProduct(
                new ProductRequest(
                        "Тестовый продукт",
                        ProductType.APPLE
                )
        );

        assertNotNull(product.getId());
        assertEquals(
                "Тестовый продукт",
                product.getName()
        );
        assertEquals(
                ProductType.APPLE,
                product.getType()
        );
    }

    @Test
    void shouldCreateDeliveryAndCalculateCost() {
        Supplier supplier = createSupplier(
                "Поставщик для поставки"
        );

        Product product = createProduct(
                "Яблоко для поставки",
                ProductType.APPLE
        );

        supplierPriceService.createSupplierPrice(
                new SupplierPriceRequest(
                        supplier.getId(),
                        product.getId(),
                        new BigDecimal("150.00"),
                        LocalDate.of(2026, 8, 1),
                        null
                )
        );

        DeliveryResponse response = deliveryService.createDelivery(
                new DeliveryRequest(
                        supplier.getId(),
                        LocalDate.of(2026, 8, 20),
                        List.of(
                                new DeliveryItemRequest(
                                        product.getId(),
                                        new BigDecimal("10.00")
                                )
                        )
                )
        );

        assertNotNull(response.id());
        assertEquals(
                supplier.getId(),
                response.supplierId()
        );
        assertEquals(
                1,
                response.items().size()
        );

        assertEquals(
                0,
                new BigDecimal("10.00").compareTo(
                        response.items().getFirst().weight()
                )
        );

        assertEquals(
                0,
                new BigDecimal("150.00").compareTo(
                        response.items().getFirst().price()
                )
        );

        assertEquals(
                0,
                new BigDecimal("1500.00").compareTo(
                        response.items().getFirst().cost()
                )
        );
    }

    @Test
    void shouldClosePreviousPriceWhenNewPriceIsCreated() {
        Supplier supplier = createSupplier(
                "Поставщик для смены цены"
        );

        Product product = createProduct(
                "Груша для смены цены",
                ProductType.PEAR
        );

        supplierPriceService.createSupplierPrice(
                new SupplierPriceRequest(
                        supplier.getId(),
                        product.getId(),
                        new BigDecimal("100.00"),
                        LocalDate.of(2026, 8, 1),
                        null
                )
        );

        supplierPriceService.createSupplierPrice(
                new SupplierPriceRequest(
                        supplier.getId(),
                        product.getId(),
                        new BigDecimal("120.00"),
                        LocalDate.of(2026, 9, 1),
                        null
                )
        );

        SupplierPrice augustPrice =
                supplierPriceService.getActualPrice(
                        supplier.getId(),
                        product.getId(),
                        LocalDate.of(2026, 8, 31)
                );

        SupplierPrice septemberPrice =
                supplierPriceService.getActualPrice(
                        supplier.getId(),
                        product.getId(),
                        LocalDate.of(2026, 9, 1)
                );

        assertEquals(
                LocalDate.of(2026, 8, 31),
                augustPrice.getDateTo()
        );

        assertEquals(
                0,
                new BigDecimal("100.00").compareTo(
                        augustPrice.getPrice()
                )
        );

        assertEquals(
                0,
                new BigDecimal("120.00").compareTo(
                        septemberPrice.getPrice()
                )
        );
    }

    @Test
    void shouldAggregateDeliveryReport() {
        Supplier supplier = createSupplier(
                "Поставщик для отчета"
        );

        Product product = createProduct(
                "Яблоко для отчета",
                ProductType.APPLE
        );

        supplierPriceService.createSupplierPrice(
                new SupplierPriceRequest(
                        supplier.getId(),
                        product.getId(),
                        new BigDecimal("200.00"),
                        LocalDate.of(2026, 8, 1),
                        null
                )
        );

        deliveryService.createDelivery(
                new DeliveryRequest(
                        supplier.getId(),
                        LocalDate.of(2026, 8, 10),
                        List.of(
                                new DeliveryItemRequest(
                                        product.getId(),
                                        new BigDecimal("10.00")
                                )
                        )
                )
        );

        deliveryService.createDelivery(
                new DeliveryRequest(
                        supplier.getId(),
                        LocalDate.of(2026, 8, 20),
                        List.of(
                                new DeliveryItemRequest(
                                        product.getId(),
                                        new BigDecimal("15.00")
                                )
                        )
                )
        );

        List<DeliveryReportResponse> report =
                reportService.getReport(
                        LocalDate.of(2026, 8, 1),
                        LocalDate.of(2026, 8, 31)
                );

        DeliveryReportResponse row = report.stream()
                .filter(item ->
                        item.supplierId().equals(supplier.getId())
                                && item.productId().equals(product.getId())
                )
                .findFirst()
                .orElseThrow();

        assertEquals(
                0,
                new BigDecimal("25.00").compareTo(
                        row.totalWeight()
                )
        );

        assertEquals(
                0,
                new BigDecimal("5000.00").compareTo(
                        row.totalCost()
                )
        );
    }

    private Supplier createSupplier(String name) {
        Supplier supplier = new Supplier();
        supplier.setName(name);

        return supplierRepository.save(supplier);
    }

    private Product createProduct(
            String name,
            ProductType type
    ) {
        Product product = new Product();
        product.setName(name);
        product.setType(type);

        return productRepository.save(product);
    }
}
