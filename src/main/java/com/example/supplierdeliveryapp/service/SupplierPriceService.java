package com.example.supplierdeliveryapp.service;

import com.example.supplierdeliveryapp.dto.SupplierPriceRequest;
import com.example.supplierdeliveryapp.entity.Product;
import com.example.supplierdeliveryapp.entity.Supplier;
import com.example.supplierdeliveryapp.entity.SupplierPrice;
import com.example.supplierdeliveryapp.exception.BadRequestException;
import com.example.supplierdeliveryapp.exception.ResourceNotFoundException;
import com.example.supplierdeliveryapp.repository.ProductRepository;
import com.example.supplierdeliveryapp.repository.SupplierPriceRepository;
import com.example.supplierdeliveryapp.repository.SupplierRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SupplierPriceService {
    private final SupplierPriceRepository supplierPriceRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;


    public SupplierPriceService(
            SupplierPriceRepository supplierPriceRepository,
            SupplierRepository supplierRepository,
            ProductRepository productRepository
    ) {
        this.supplierPriceRepository = supplierPriceRepository;
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
    }

    public SupplierPrice getActualPrice(
            Long supplierId,
            Long productId,
            LocalDate date
    ) {
        return supplierPriceRepository.findActualPrice(
                supplierId,
                productId,
                date
        ).orElseThrow(() ->
                new ResourceNotFoundException("Актуальная цена не найдена")
        );
    }

    @Transactional
    public SupplierPrice createSupplierPrice(SupplierPriceRequest request) {
        Supplier supplier = supplierRepository.findById(request.supplierId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Поставщик не найден")
                );
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Товар не найден")
                );
        if (request.dateTo() != null && request.dateFrom().isAfter(request.dateTo())) {
            throw new BadRequestException(
                    "Дата окончания действия цены не может быть раньше даты начала"
            );
        }
        if (supplierPriceRepository.existsBySupplier_IdAndProduct_IdAndDateFrom(
                supplier.getId(),
                product.getId(),
                request.dateFrom()
        )) {
            throw new BadRequestException(
                    "Цена для этого поставщика и товара на указанную дату уже существует"
            );
        }

        if (supplierPriceRepository.existsBySupplier_IdAndProduct_IdAndDateFromAfter(
                supplier.getId(),
                product.getId(),
                request.dateFrom()
        )) {
            throw new BadRequestException(
                    "Нельзя добавить цену задним числом: существует более поздняя цена"
            );
        }
        supplierPriceRepository.findActualPrice(
                supplier.getId(),
                product.getId(),
                request.dateFrom()
        ).ifPresent(currentPrice -> {
            currentPrice.setDateTo(request.dateFrom().minusDays(1));
            supplierPriceRepository.save(currentPrice);
        });
        SupplierPrice supplierPrice = new SupplierPrice();
        supplierPrice.setSupplier(supplier);
        supplierPrice.setProduct(product);
        supplierPrice.setPrice(request.price());
        supplierPrice.setDateFrom(request.dateFrom());
        supplierPrice.setDateTo(request.dateTo());

        return supplierPriceRepository.save(supplierPrice);
    }
    public List<SupplierPrice> getAllSupplierPrices() {
        return supplierPriceRepository.findAll();
    }
}
