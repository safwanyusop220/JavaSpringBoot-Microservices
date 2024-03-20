package com.beans.inventoryservice.service;

import com.beans.inventoryservice.dto.InventoryDto;
import com.beans.inventoryservice.dto.InventoryResponse;
import com.beans.inventoryservice.entity.Inventory;
import com.beans.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<Integer> productId){
        return inventoryRepository.findByProductIdIn(productId).stream()
                .map(inventory ->
                        InventoryResponse.builder()
                                .productId(inventory.getProductId())
                                .isInStock(inventory.getAvailability())
                                .build()
                ).toList();
    }

    public List<Inventory> getInventory() {
        return inventoryRepository.findAll();
    }

    public List<Inventory> getInventoryById(List<Long> inventoryId) {
        List<Inventory> inventories = inventoryRepository.findAllById(inventoryId);
        if (inventories.isEmpty()) {
            throw new ProductNotFoundException("No inventory found with the provided IDs: " + inventoryId);
        }
        return inventories;
    }

    public List<Inventory> addInventory(List<Inventory> inventoryList) {
        return inventoryRepository.saveAll(inventoryList);
    }

    public InventoryDto updateInventory(Long id, Boolean availability) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + id + " does not exist"));

        inventory.setAvailability(availability);
        Inventory savedInventory = inventoryRepository.save(inventory);

        return convertToDto(savedInventory);
    }

    private InventoryDto convertToDto(Inventory inventory) {
        InventoryDto inventoryDto = new InventoryDto();
        inventoryDto.setId(inventory.getId());
        inventoryDto.setProductId(inventory.getProductId());
        inventoryDto.setAvailability(inventory.getAvailability());
        return inventoryDto;
    }

    public void deleteInventory(Long id) {
        Inventory inventory = inventoryRepository
                .findById(id)
                .orElseThrow(()-> new ProductNotFoundException("Product with ID " + id + " does not exist"));

        inventoryRepository.deleteById(id);
    }

    public static class ProductNotFoundException extends RuntimeException {
        public ProductNotFoundException(String message) {
            super(message);
        }
    }

}
