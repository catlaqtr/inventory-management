package com.example.inventory_management.controller;

import com.example.inventory_management.dao.InventoryReportDao;
import com.example.inventory_management.service.InventoryService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReportController {

    private final InventoryReportDao
            inventoryReportDao;

    private final InventoryService
            inventoryService;

    public ReportController(
            InventoryReportDao inventoryReportDao,
            InventoryService inventoryService
    ) {
        this.inventoryReportDao =
                inventoryReportDao;

        this.inventoryService =
                inventoryService;
    }

    @GetMapping("/reports")
    public String showReports(Model model) {

        model.addAttribute(
                "totalProducts",
                inventoryReportDao
                        .getTotalProducts()
        );

        model.addAttribute(
                "totalQuantity",
                inventoryReportDao
                        .getTotalQuantity()
        );

        model.addAttribute(
                "totalValue",
                inventoryReportDao
                        .getTotalInventoryValue()
        );

        model.addAttribute(
                "lowStockProducts",
                inventoryService
                        .getLowStockProducts()
        );

        model.addAttribute(
                "transactions",
                inventoryService
                        .getAllTransactions()
        );

        return "reports";
    }
}