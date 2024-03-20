package com.beans.cartservice.controller;

import com.beans.cartservice.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CartService.ProductNotAvailableException.class)
    public ResponseEntity<String> handleProductNotAvailableException(CartService.ProductNotAvailableException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(CartService.ProductNotFoundException.class)
    public ResponseEntity<String> handleProductNotFoundException(CartService.ProductNotFoundException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());    }
}
