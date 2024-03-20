package com.beans.inventoryservice.controller;

import com.beans.inventoryservice.dto.InventoryDto;
import com.beans.inventoryservice.dto.InventoryResponse;
import com.beans.inventoryservice.entity.Inventory;
import com.beans.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam List<Integer> productId) {
        return inventoryService.isInStock(productId);
    }

    @GetMapping("/all")
    public List<Inventory> getInventory(){
        return inventoryService.getInventory();
    }

    @GetMapping("/{inventoryId}")
    public List<Inventory> getInventoryById(@PathVariable List<Long> inventoryId){
        return inventoryService.getInventoryById(inventoryId);
    }

    @PostMapping("/create")
    public List<Inventory> addInventory(@RequestBody List<Inventory> inventoryList){
        return  inventoryService.addInventory(inventoryList);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<InventoryDto> updateInventory(@PathVariable Long id, @RequestBody Boolean availability) {
        InventoryDto inventoryDto = inventoryService.updateInventory(id, availability);
        return ResponseEntity.ok(inventoryDto);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteInventory(@PathVariable Long id){
        inventoryService.deleteInventory(id);
        return ResponseEntity.ok("Inventory is deleted successfully!");
    }

}
