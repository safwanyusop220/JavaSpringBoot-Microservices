package com.beans.productservice.controller;

import com.beans.productservice.dto.ProductDto;
import com.beans.productservice.entity.Product;
import com.beans.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/addProducts")
    public List<Product> addProducts(@RequestBody List<Product> productList){
        return  productService.addProducts(productList);
    }

    @GetMapping
    public List<Product> getProducts(){
        return productService.getProducts();
    }

    @GetMapping("getProducts/{productIds}")
    public List<Product> getProductByIds(@PathVariable List<Long> productIds){
        return productService.getProductsById(productIds);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @RequestBody Map<String, String> request){
        String productName = request.get("productName");
        ProductDto productDto = productService.updateProduct(id, productName);
        return ResponseEntity.ok(productDto);
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product is deleted successfully!");
    }
}
