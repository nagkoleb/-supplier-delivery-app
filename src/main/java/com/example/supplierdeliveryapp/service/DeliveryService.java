package com.example.supplierdeliveryapp.service;

import com.example.supplierdeliveryapp.dto.DeliveryItemRequest;
import com.example.supplierdeliveryapp.dto.DeliveryItemResponse;
import com.example.supplierdeliveryapp.dto.DeliveryRequest;
import com.example.supplierdeliveryapp.dto.DeliveryResponse;
import com.example.supplierdeliveryapp.entity.*;
import com.example.supplierdeliveryapp.exception.ResourceNotFoundException;
import com.example.supplierdeliveryapp.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class DeliveryService {
    private final DeliveryItemRepository deliveryItemRepository;
    private final DeliveryRepository deliveryRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final SupplierPriceService supplierPriceService;

    public DeliveryService(DeliveryItemRepository deliveryItemRepository, DeliveryRepository deliveryRepository,
                           ProductRepository productRepository, SupplierRepository supplierRepository,
                           SupplierPriceService supplierPriceService) {
        this.deliveryItemRepository = deliveryItemRepository;
        this.deliveryRepository = deliveryRepository;
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.supplierPriceService = supplierPriceService;
    }

    @Transactional
    public DeliveryResponse createDelivery(DeliveryRequest request) {
        Supplier supplier =
                supplierRepository.findById(request.supplierId()).orElseThrow(() ->
                        new ResourceNotFoundException("Поставщик не найден")
                );
        Delivery delivery = new Delivery();
        delivery.setSupplier(supplier);
        delivery.setDeliveryDate(request.deliveryDate());
        delivery = deliveryRepository.save(delivery);
        List<DeliveryItemResponse> itemResponses = new ArrayList<>();
        for (DeliveryItemRequest itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Товар не найден")
                    );
            SupplierPrice supplierPrice =
                    supplierPriceService.getActualPrice(supplier.getId(), product.getId(), request.deliveryDate());
            BigDecimal price = supplierPrice.getPrice();
            BigDecimal cost = price
                    .multiply(itemRequest.weight())
                    .setScale(2, RoundingMode.HALF_UP);
            DeliveryItem deliveryItem = new DeliveryItem();
            deliveryItem.setDelivery(delivery);
            deliveryItem.setProduct(product);
            deliveryItem.setWeight(itemRequest.weight());
            deliveryItem.setPrice(price);
            deliveryItem.setCost(cost);

            deliveryItemRepository.save(deliveryItem);
            itemResponses.add(new DeliveryItemResponse(
                    product.getId(),
                    product.getName(),
                    itemRequest.weight(),
                    price,
                    cost
            ));
        }
        return new DeliveryResponse(
                delivery.getId(),
                supplier.getId(),
                supplier.getName(),
                delivery.getDeliveryDate(),
                itemResponses
        );


    }
}
