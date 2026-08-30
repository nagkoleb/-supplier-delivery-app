package com.example.supplierdeliveryapp.service;

import com.example.supplierdeliveryapp.dto.SupplierRequest;
import com.example.supplierdeliveryapp.entity.Supplier;
import com.example.supplierdeliveryapp.exception.BadRequestException;
import com.example.supplierdeliveryapp.exception.ConflictException;
import com.example.supplierdeliveryapp.exception.ResourceNotFoundException;
import com.example.supplierdeliveryapp.repository.DeliveryRepository;
import com.example.supplierdeliveryapp.repository.SupplierPriceRepository;
import com.example.supplierdeliveryapp.repository.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierPriceRepository supplierPriceRepository;
    private final DeliveryRepository deliveryRepository;

    public SupplierService(SupplierRepository supplierRepository, SupplierPriceRepository supplierPriceRepository,
                           DeliveryRepository deliveryRepository) {
        this.supplierRepository = supplierRepository;
        this.supplierPriceRepository = supplierPriceRepository;
        this.deliveryRepository = deliveryRepository;
    }

    public Supplier createSupplier(SupplierRequest request) {
        if (supplierRepository.existsByName(request.name())) {
            throw new BadRequestException(
                    "Поставщик с таким именем уже существует"
            );
        }
        Supplier supplier = new Supplier();
        supplier.setName(request.name());

        return supplierRepository.save(supplier);
    }
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }
    public Supplier updateSupplier(Long id, SupplierRequest request) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Поставщик не найден")
                );

        if (!supplier.getName().equals(request.name())
                && supplierRepository.existsByName(request.name())) {
            throw new BadRequestException(
                    "Поставщик с таким именем уже существует"
            );
        }

        supplier.setName(request.name());
        return supplierRepository.save(supplier);
    }
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Поставщик не найден")
                );

        if (supplierPriceRepository.existsBySupplier_Id(id)
                || deliveryRepository.existsBySupplier_Id(id)) {
            throw new ConflictException(
                    "Нельзя удалить поставщика: с ним связаны цены или поставки"
            );
        }

        supplierRepository.delete(supplier);
    }
}