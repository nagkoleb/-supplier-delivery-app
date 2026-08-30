package com.example.supplierdeliveryapp.controller;

import com.example.supplierdeliveryapp.dto.ProductRequest;
import com.example.supplierdeliveryapp.dto.ProductResponse;
import com.example.supplierdeliveryapp.entity.Product;
import com.example.supplierdeliveryapp.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ProductResponse createProduct(
            @Valid @RequestBody ProductRequest request
    ) {
        Product product = productService.createProduct(request);

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getType()
        );
    }

    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts()
                .stream()
                .map(product -> new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getType()
                ))
                .toList();
    }

    @PutMapping("/{id}")
    public ProductResponse updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request
    ) {
        Product product = productService.updateProduct(id, request);

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getType()
        );
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
