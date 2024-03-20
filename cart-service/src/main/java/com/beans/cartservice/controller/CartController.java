package com.beans.cartservice.controller;

import com.beans.cartservice.dto.CartDto;
import com.beans.cartservice.entity.CartEntity;
import com.beans.cartservice.model.CartRequest;
import com.beans.cartservice.model.CartResponse;
import com.beans.cartservice.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/with-product/{userId}")
    public ResponseEntity createCartWithProduct(@PathVariable Long userId, @RequestBody List<CartRequest> reqProductList) {
        CartResponse response = cartService.createCartWithProduct(userId, reqProductList);
        return new ResponseEntity(response, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @GetMapping("/updated-cart/{userId}")
    public ResponseEntity updatedCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.updatedCart(userId));
    }

    @GetMapping
    public List<CartEntity> getCarts(){
        return cartService.getCarts();
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<CartDto> updateCart(@PathVariable Long userId, @RequestBody Map<String, String> request){
        String status = request.get("status");
        CartDto cartDto = cartService.updateCart(userId, status);
        return ResponseEntity.ok(cartDto);
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> deleteCart(@PathVariable Long userId){
        cartService.deleteCart(userId);
        return ResponseEntity.ok("Cart is deleted successfully!");
    }

    @PostMapping("/add-product/{userId}")
    public ResponseEntity<List<CartResponse>> addProductToCart(@PathVariable Long userId, @RequestBody List<CartRequest> cartRequests) {
        List<CartResponse> responses = new ArrayList<>();
        for (CartRequest cartRequest : cartRequests) {
            CartResponse response = cartService.addProductToCart(userId, cartRequests);
            responses.add(response);
        }
        return new ResponseEntity<>(responses, HttpStatus.CREATED);
    }

    @PostMapping("/remove-product/{userId}")
    public ResponseEntity<CartResponse> removeProductFromCart(@PathVariable Long userId, @RequestBody List<CartRequest> cartRequests) {
        CartResponse response = cartService.removeProductFromCart(userId, cartRequests);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }



}
