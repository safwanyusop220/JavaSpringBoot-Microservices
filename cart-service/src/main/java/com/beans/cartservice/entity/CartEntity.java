package com.beans.cartservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CART")
@Builder
public class CartEntity {
    @Id
    private Long userId;
    private Long cartId;
    private Integer totalItems;
    private Double totalCost;
    private String products;
    private String status;
}
