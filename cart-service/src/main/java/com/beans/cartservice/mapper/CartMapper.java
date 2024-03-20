package com.beans.cartservice.mapper;

import com.beans.cartservice.dto.CartDto;
import com.beans.cartservice.entity.CartEntity;

public class CartMapper {

    public static CartEntity mapToCart(CartDto cartDto){
        CartEntity cartEntity = new CartEntity(
                cartDto.getUserId(),
                cartDto.getCartId(),
                cartDto.getTotalItems(),
                cartDto.getTotalCost(),
                cartDto.getProducts(),
                cartDto.getStatus()
        );
        return cartEntity;
    }

    public static CartDto mapToCartDto(CartEntity cartEntity){
        CartDto cartDto = new CartDto(
                cartEntity.getUserId(),
                cartEntity.getCartId(),
                cartEntity.getTotalItems(),
                cartEntity.getTotalCost(),
                cartEntity.getProducts(),
                cartEntity.getStatus()
        );
        return cartDto;
    }

}
