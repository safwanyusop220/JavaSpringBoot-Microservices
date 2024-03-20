package com.beans.cartservice.service;

import com.beans.cartservice.dto.CartDto;
import com.beans.cartservice.dto.InventoryResponse;
import com.beans.cartservice.entity.CartEntity;
import com.beans.cartservice.mapper.CartMapper;
import com.beans.cartservice.model.CartRequest;
import com.beans.cartservice.model.CartResponse;
import com.beans.cartservice.model.Product;
import com.beans.cartservice.repository.CartRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.core.type.TypeReference;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartService {

    @Autowired
    private WebClient.Builder webBuilder;

    @Autowired
    private CartRepository cartRepository;


    public CartResponse createCartWithProduct(Long userId, List<CartRequest> cartRequestList) {

        //Call product service
        ObjectMapper mapper = new ObjectMapper();
        String productServiceUrl = "http://server1:8081/products/getProducts/" + cartRequestList.stream().map(e->String.valueOf(e.getProductId())).collect(Collectors.joining(","));
        List<Product> productServiceList = webBuilder.build()
                .get()
                .uri(productServiceUrl)
                .retrieve()
                .bodyToFlux(Product.class)
                .collectList()
                .block();

        System.out.println(productServiceUrl);
        System.out.println("ProductServiceList -> " +productServiceList);

        //Calculate totalCost
        final Double[] totalCost = {0.0};
        productServiceList.forEach(psl -> {
            cartRequestList.forEach(scr -> {
                if (psl.getProductId() == scr.getProductId()) {
                    psl.setQuantity(scr.getQuantity());
                    totalCost[0] = totalCost[0] + psl.getAmount() * scr.getQuantity();
                }
            });
        });

        //Create cart entity
        CartEntity cartEntity = null;
        try {
            cartEntity = CartEntity.builder()
                    .userId(userId)
                    .cartId((long) (Math.random() * Math.pow(10, 10)))
                    .totalItems(productServiceList.size())
                    .totalCost(totalCost[0])
                    .status("accepted")
                    .products(mapper.writeValueAsString(productServiceList))
                    .build();
        } catch (JsonProcessingException e){
            throw new RuntimeException(e);
        }

        List<String> productIds = cartRequestList.stream()
                .map(CartRequest::getProductId)
                .map(String::valueOf)
                .collect(Collectors.toList());

        InventoryResponse[] inventoryResponsArray = webBuilder.build().get()
                .uri("http://server2:8085/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("productId", productIds).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean allProductInStock = Arrays.stream(inventoryResponsArray).allMatch(InventoryResponse::isInStock);

        // save CartEntity
        if(allProductInStock){
            cartEntity = cartRepository.save(cartEntity);
        } else {
            throw new ProductNotAvailableException("Product is not in stock, please try again later!");
        }


        // Create api response
        CartResponse response = CartResponse.builder()
                .cartId(cartEntity.getCartId())
                .userId(cartEntity.getUserId())
                .totalItems(cartEntity.getTotalItems())
                .totalCost(cartEntity.getTotalCost())
                .status(cartEntity.getStatus())
                .products(productServiceList)
                .build();
        return response;
    }

    public class ProductNotAvailableException extends RuntimeException {
        public ProductNotAvailableException(String message) {
            super(message);
        }
    }

    public class ProductNotFoundException extends RuntimeException {
        public ProductNotFoundException(String message) {
            super(message);
        }
    }

    public List<CartResponse> getCart(Long userId) {
        ObjectMapper mapper = new ObjectMapper();
        List<CartEntity> cartEntities =  cartRepository.findByUserId(userId);

        if (cartEntities.isEmpty()) {
            throw new ProductNotFoundException("User with ID " + userId + " does not have any carts.");
        }

        List<CartResponse> cartResponses = cartEntities.stream()
                .map(ce->{
                    try {
                        return CartResponse.builder()
                                .cartId(ce.getCartId())
                                .userId(ce.getUserId())
                                .totalItems(ce.getTotalItems())
                                .totalCost(ce.getTotalCost())
                                .status(ce.getStatus())
                                .products(mapper.readValue(ce.getProducts(), List.class))
                                .build();
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
        return cartResponses;
    }

    public List<CartEntity> getCarts() {
        return cartRepository.findAll();
    }

    public CartDto updateCart(Long userId, String status) {
        CartEntity cartEntity = cartRepository
                .findById(userId)
                .orElseThrow(()-> new ProductNotFoundException("User with ID " + userId + " does not exist"));
        cartEntity.setStatus(status);
        CartEntity savedCart = cartRepository.save(cartEntity);
        return CartMapper.mapToCartDto(savedCart);
    }

    public void deleteCart(Long userId) {
        CartEntity cartEntity = cartRepository
                .findById(userId)
                .orElseThrow(()-> new ProductNotFoundException("User with ID " + userId + " does not exist"));
        cartRepository.deleteById(userId);
    }

    public List<CartResponse> getUpdatedCart(Long userId) {
        ObjectMapper mapper = new ObjectMapper();
        List<CartEntity> cartEntities = cartRepository.findByUserId(userId);

        if (cartEntities.isEmpty()) {
            throw new ProductNotFoundException("User with ID " + userId + " does not have any carts.");
        }

        List<CartResponse> cartResponses = cartEntities.stream()
                .map(ce -> {
                    try {
                        return CartResponse.builder()
                                .cartId(ce.getCartId())
                                .userId(ce.getUserId())
                                .totalItems(ce.getTotalItems())
                                .totalCost(ce.getTotalCost())
                                .status(ce.getStatus())
                                .products(mapper.readValue(ce.getProducts(), List.class))
                                .build();
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());

        return cartResponses;
    }

    // Add Product To Cart
    public CartResponse addProductToCart(Long userId, List<CartRequest> cartRequestList) {
        // Retrieve existing cart entity for the given userId
        CartEntity cartEntity = cartRepository.findById(userId)
                .orElseThrow(() -> new ProductNotFoundException("User with ID " + userId + " does not exist"));

        // Call product service to get product details for new products only
        ObjectMapper mapper = new ObjectMapper();
        String productServiceUrl = "http://server1/products/getProducts/"  + cartRequestList.stream().map(e -> String.valueOf(e.getProductId())).collect(Collectors.joining(","));
        List<Product> newProductServiceList = webBuilder.build()
                .get()
                .uri(productServiceUrl)
                .retrieve()
                .bodyToFlux(Product.class)
                .collectList()
                .block();

        // Merge new products with existing products in the cart
        List<Product> existingProducts = new ArrayList<>();
        try {
            existingProducts = mapper.readValue(cartEntity.getProducts(), new TypeReference<List<Product>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Error parsing existing products JSON", e);
        }

        // Update existing products with new quantities
        for (Product existingProduct : existingProducts) {
            for (CartRequest cartRequest : cartRequestList) {
                if (existingProduct.getProductId().equals(cartRequest.getProductId())) {
                    existingProduct.setQuantity(existingProduct.getQuantity() + cartRequest.getQuantity());
                    break; // Move to the next existing product
                }
            }
        }

        // Add new products to the merged list
        existingProducts.addAll(newProductServiceList);

        // Calculate totalCost based on merged list of products
        double totalCost = existingProducts.stream()
                .mapToDouble(product -> product.getAmount() * product.getQuantity())
                .sum();

        // Update cart entity
        cartEntity.setTotalItems(existingProducts.size());
        cartEntity.setTotalCost(totalCost);
        cartEntity.setStatus("accepted");

        // Serialize merged list of products to JSON string and update the products field
        try {
            String productJson = mapper.writeValueAsString(existingProducts);
            cartEntity.setProducts(productJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing products to JSON", e);
        }

        // Save updated cart entity
        cartEntity = cartRepository.save(cartEntity);

        // Create API response
        CartResponse response = CartResponse.builder()
                .cartId(cartEntity.getCartId())
                .userId(cartEntity.getUserId())
                .totalItems(cartEntity.getTotalItems())
                .totalCost(cartEntity.getTotalCost())
                .status(cartEntity.getStatus())
                .products(existingProducts)
                .build();
        return response;
    }

    // Remove Product From Cart
    public CartResponse removeProductFromCart(Long userId, List<CartRequest> cartRequestList) {
        // Retrieve existing cart entity for the given userId
        CartEntity cartEntity = cartRepository.findById(userId)
                .orElseThrow(() -> new ProductNotFoundException("User with ID " + userId + " does not exist"));

        // Call product service to get product details for new products only
        ObjectMapper mapper = new ObjectMapper();

        // Retrieve existing products from the cart
        List<Product> existingProducts = new ArrayList<>();
        try {
            existingProducts = mapper.readValue(cartEntity.getProducts(), new TypeReference<List<Product>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Error parsing existing products JSON", e);
        }

        // Iterate over the cart request list and remove products from existingProducts
        for (CartRequest cartRequest : cartRequestList) {
            // Find the product in existingProducts based on productId
            for (Iterator<Product> iterator = existingProducts.iterator(); iterator.hasNext();) {
                Product existingProduct = iterator.next();
                if (existingProduct.getProductId().equals(cartRequest.getProductId())) {
                    // Decrease quantity or remove the product if the requested quantity is greater or equal to the existing quantity
                    if (existingProduct.getQuantity() > cartRequest.getQuantity()) {
                        existingProduct.setQuantity(existingProduct.getQuantity() - cartRequest.getQuantity());
                    } else {
                        iterator.remove();
                    }
                    break; // Move to the next cart request
                }
            }
        }

        // Calculate totalCost based on updated list of products
        double totalCost = existingProducts.stream()
                .mapToDouble(product -> product.getAmount() * product.getQuantity())
                .sum();

        // Update cart entity
        cartEntity.setTotalItems(existingProducts.size());
        cartEntity.setTotalCost(totalCost);
        cartEntity.setStatus("accepted");

        // Serialize updated list of products to JSON string and update the products field
        try {
            String productJson = mapper.writeValueAsString(existingProducts);
            cartEntity.setProducts(productJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing products to JSON", e);
        }

        // Save updated cart entity
        cartEntity = cartRepository.save(cartEntity);

        // Create API response
        CartResponse response = CartResponse.builder()
                .cartId(cartEntity.getCartId())
                .userId(cartEntity.getUserId())
                .totalItems(cartEntity.getTotalItems())
                .totalCost(cartEntity.getTotalCost())
                .status(cartEntity.getStatus())
                .products(existingProducts)
                .build();
        return response;
    }

    public List<CartResponse> updatedCart(Long userId) {
        ObjectMapper mapper = new ObjectMapper();
        List<CartEntity> cartEntities =  cartRepository.findByUserId(userId);
        List<CartResponse> cartResponses = cartEntities.stream()
                .map(ce->{
                    try {
                        return CartResponse.builder()
                                .cartId(ce.getCartId())
                                .userId(ce.getUserId())
                                .totalItems(ce.getTotalItems())
                                .totalCost(ce.getTotalCost())
                                .status(ce.getStatus())
                                .products(mapper.readValue(ce.getProducts(), List.class))
                                .build();
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
        return cartResponses;
    }
}
