package com.beans.inventoryservice;

import com.beans.inventoryservice.entity.Inventory;
import com.beans.inventoryservice.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryServiceApplication {

    public static void main(String[] args){
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadData(InventoryRepository inventoryRepository){
        return args -> {
            Inventory inventory = new Inventory();
            inventory.setProductId(1);
            inventory.setAvailability(true);

            Inventory inventory1 = new Inventory();
            inventory1.setProductId(2);
            inventory1.setAvailability(false);

            inventoryRepository.save(inventory);
            inventoryRepository.save(inventory1);
        };
    }
}
