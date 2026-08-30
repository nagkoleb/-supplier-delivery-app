package com.example.supplierdeliveryapp.controller;

import com.example.supplierdeliveryapp.dto.DeliveryRequest;
import com.example.supplierdeliveryapp.dto.DeliveryResponse;
import com.example.supplierdeliveryapp.service.DeliveryService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {
    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @PostMapping
    public DeliveryResponse createDelivery(
            @Valid @RequestBody DeliveryRequest deliveryRequest
    ) {
        return deliveryService.createDelivery(deliveryRequest);
    }
}
