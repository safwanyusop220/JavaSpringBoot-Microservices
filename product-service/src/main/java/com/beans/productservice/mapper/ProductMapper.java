package com.beans.productservice.mapper;

import com.beans.productservice.dto.ProductDto;
import com.beans.productservice.entity.Product;

public class ProductMapper {

    public static Product mapToProduct(ProductDto productDto) {
        Product product = new Product(
                productDto.getProductId(),
                productDto.getProductName(),
                productDto.getQuantity(),
                productDto.getAmount(),
                productDto.getProductCode()
        );
        return product;
    }

    public static ProductDto mapToProductDto(Product product){
        ProductDto productDto = new ProductDto(
                product.getProductId(),
                product.getProductName(),
                product.getQuantity(),
                product.getAmount(),
                product.getProductCode()
        );
        return productDto;
    }
}
