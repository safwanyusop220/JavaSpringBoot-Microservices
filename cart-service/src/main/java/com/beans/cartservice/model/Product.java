package com.beans.cartservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Product {
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double amount;
    private String productCode;

    @JsonCreator
    public Product(@JsonProperty("productId") Long productId,
                   @JsonProperty("productName") String productName,
                   @JsonProperty("quantity") Integer quantity,
                   @JsonProperty("amount") Double amount,
                   @JsonProperty("productCode") String productCode
    ){
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.amount = amount;
        this.productCode = productCode;
    }
}
