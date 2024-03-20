package com.beans.inventoryservice.repository;

import com.beans.inventoryservice.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findByProductIdIn(List<Integer> productId);
}
