package com.example.inventory_management.controller;

import com.example.inventory_management.service.InventoryService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/transactions")
public class StockTransactionController {

    private final InventoryService inventoryService;

    public StockTransactionController(
            InventoryService inventoryService
    ) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public String showTransactionHistory(
            Model model
    ) {
        model.addAttribute(
                "transactions",
                inventoryService
                        .getAllTransactions()
        );

        return "transactions";
    }

    @GetMapping("/add")
    public String showTransactionForm(
            Model model
    ) {
        model.addAttribute(
                "products",
                inventoryService
                        .getAllProducts()
        );

        return "transaction-form";
    }

    @PostMapping("/save")
    public String saveTransaction(
            @RequestParam Integer productId,
            @RequestParam String type,
            @RequestParam int quantity,
            @RequestParam(
                    required = false
            )
            String note,
            Model model
    ) {
        try {

            inventoryService
                    .recordStockTransaction(
                            productId,
                            type,
                            quantity,
                            note
                    );

            return "redirect:/transactions";

        } catch (RuntimeException exception) {

            model.addAttribute(
                    "error",
                    exception.getMessage()
            );

            model.addAttribute(
                    "products",
                    inventoryService
                            .getAllProducts()
            );

            return "transaction-form";
        }
    }
}