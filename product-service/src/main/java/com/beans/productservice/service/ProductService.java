package com.beans.productservice.service;

import com.beans.productservice.dto.ProductDto;
import com.beans.productservice.entity.Product;
import com.beans.productservice.mapper.ProductMapper;
import com.beans.productservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> addProducts(List<Product> productList) {
        return productRepository.saveAll(productList);
    }

    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    public List<Product> getProductsById(List<Long> productIds) {
        List<Product> products = productRepository.findAllById(productIds);

        // Check if all requested product IDs were found
        List<Long> foundProductIds = products.stream()
                .map(Product::getProductId)
                .collect(Collectors.toList());

        // Collect IDs of products not found
        List<Long> notFoundProductIds = productIds.stream()
                .filter(id -> !foundProductIds.contains(id))
                .collect(Collectors.toList());

        // If any product ID was not found, throw an exception
        if (!notFoundProductIds.isEmpty()) {
            throw new ProductNotFoundException("Product(s) not found with IDs: " + notFoundProductIds);
        }

        return products;
    }
    public class ProductNotFoundException extends RuntimeException {
        public ProductNotFoundException(String message) {
            super(message);
        }
    }


    public ProductDto updateProduct(Long id, String productName) {
        Product product = productRepository
                .findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + id + " does not exist"));
        product.setProductName(productName);
        Product savedProduct = productRepository.save(product);
        return ProductMapper.mapToProductDto(savedProduct);

    }
    public void deleteProduct(Long id) {
        Product product = productRepository
                .findById(id)
                .orElseThrow(()-> new ProductNotFoundException("Product with ID " + id + " does not exist"));

        productRepository.deleteById(id);
    }
}
