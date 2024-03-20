package com.beans.cartservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartDto {
    private Long userId;
    private Long cartId;
    private Integer totalItems;
    private Double totalCost;
    private String products;
    private String status;
}
