package com.example.supplierdeliveryapp.service;

import com.example.supplierdeliveryapp.dto.ProductRequest;
import com.example.supplierdeliveryapp.entity.Product;
import com.example.supplierdeliveryapp.exception.BadRequestException;
import com.example.supplierdeliveryapp.exception.ConflictException;
import com.example.supplierdeliveryapp.exception.ResourceNotFoundException;
import com.example.supplierdeliveryapp.repository.DeliveryItemRepository;
import com.example.supplierdeliveryapp.repository.ProductRepository;
import com.example.supplierdeliveryapp.repository.SupplierPriceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final SupplierPriceRepository supplierPriceRepository;
    private final DeliveryItemRepository deliveryItemRepository;

    public ProductService(ProductRepository productRepository, SupplierPriceRepository supplierPriceRepository,
                          DeliveryItemRepository deliveryItemRepository) {
        this.productRepository = productRepository;
        this.supplierPriceRepository = supplierPriceRepository;
        this.deliveryItemRepository = deliveryItemRepository;
    }

    public Product createProduct(ProductRequest request) {
        Product product = new Product();
        if (productRepository.existsByName(request.name())) {
            throw new BadRequestException(
                    "Товар с таким названием уже существует"
            );
        }
        product.setName(request.name());
        product.setType(request.type());
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Товар не найден")
                );

        if (!product.getName().equals(request.name())
                && productRepository.existsByName(request.name())) {
            throw new BadRequestException(
                    "Товар с таким названием уже существует"
            );
        }

        product.setName(request.name());
        product.setType(request.type());

        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Товар не найден")
                );

        if (supplierPriceRepository.existsByProduct_Id(id)
                || deliveryItemRepository.existsByProduct_Id(id)) {
            throw new ConflictException(
                    "Нельзя удалить товар: с ним связаны цены или поставки"
            );
        }

        productRepository.delete(product);
    }
}
