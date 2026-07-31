package com.example.inventory_management.controller;

import com.example.inventory_management.model.Product;
import com.example.inventory_management.service.InventoryService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductRestController {

    private final InventoryService inventoryService;

    public ProductRestController(
            InventoryService inventoryService
    ) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public List<Product> getProducts(
            @RequestParam(
                    required = false
            )
            String keyword
    ) {
        return inventoryService
                .searchProducts(keyword);
    }
}