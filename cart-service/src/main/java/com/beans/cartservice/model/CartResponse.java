package com.beans.cartservice.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CartResponse {
    private Long userId;
    private Long cartId;
    private Integer totalItems;
    private Double totalCost;
    private String status;
    private List<Product> products;
}
