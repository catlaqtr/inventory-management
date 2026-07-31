package com.example.inventory_management.controller;

import com.example.inventory_management.model.Product;
import com.example.inventory_management.service.InventoryService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final InventoryService inventoryService;

    public ProductController(
            InventoryService inventoryService
    ) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public String showProducts(
            @RequestParam(
                    required = false
            )
            String keyword,
            Model model
    ) {
        model.addAttribute(
                "products",
                inventoryService
                        .searchProducts(keyword)
        );

        model.addAttribute(
                "lowStockProducts",
                inventoryService
                        .getLowStockProducts()
        );

        model.addAttribute(
                "keyword",
                keyword
        );

        return "products";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {

        model.addAttribute(
                "product",
                new Product()
        );

        model.addAttribute(
                "formTitle",
                "Add Product"
        );

        return "product-form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(
            @PathVariable Integer id,
            Model model
    ) {
        model.addAttribute(
                "product",
                inventoryService
                        .getProductById(id)
        );

        model.addAttribute(
                "formTitle",
                "Update Product"
        );

        return "product-form";
    }

    @PostMapping("/save")
    public String saveProduct(
            @ModelAttribute Product product,
            Model model
    ) {
        try {

            if (product.getId() == null) {

                inventoryService
                        .addProduct(product);

            } else {

                inventoryService
                        .updateProduct(
                                product.getId(),
                                product
                        );
            }

            return "redirect:/products";

        } catch (RuntimeException exception) {

            model.addAttribute(
                    "error",
                    exception.getMessage()
            );

            model.addAttribute(
                    "formTitle",
                    product.getId() == null
                            ? "Add Product"
                            : "Update Product"
            );

            return "product-form";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(
            @PathVariable Integer id
    ) {
        inventoryService.deleteProduct(id);

        return "redirect:/products";
    }
}