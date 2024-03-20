package com.beans.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double amount;
    private String productCode;
}
