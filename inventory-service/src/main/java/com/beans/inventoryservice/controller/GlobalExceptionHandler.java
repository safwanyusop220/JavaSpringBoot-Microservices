package com.beans.inventoryservice.controller;
import com.beans.inventoryservice.service.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InventoryService.ProductNotFoundException.class)
    public ResponseEntity<String> handleProductNotFoundException(InventoryService.ProductNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
