package com.example.inventory_management.controller;

import com.example.inventory_management.dao.InventoryReportDao;
import com.example.inventory_management.security.CompanyContext;
import com.example.inventory_management.service.BillingService;
import com.example.inventory_management.service.InventoryService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReportController {

    private final InventoryReportDao inventoryReportDao;
    private final InventoryService inventoryService;
    private final BillingService billingService;

    public ReportController(
            InventoryReportDao inventoryReportDao,
            InventoryService inventoryService,
            BillingService billingService
    ) {
        this.inventoryReportDao = inventoryReportDao;
        this.inventoryService = inventoryService;
        this.billingService = billingService;
    }

    @GetMapping("/reports")
    public String showReports(Model model) {
        Long companyId = CompanyContext.requireCompanyId();

        model.addAttribute(
                "totalProducts",
                inventoryReportDao.getTotalProducts(companyId)
        );
        model.addAttribute(
                "totalQuantity",
                inventoryReportDao.getTotalQuantity(companyId)
        );
        model.addAttribute(
                "totalValue",
                inventoryReportDao.getTotalInventoryValue(companyId)
        );
        model.addAttribute(
                "invoiceCount",
                billingService.countInvoicesForCurrentCompany()
        );
        model.addAttribute(
                "totalSales",
                billingService.getTotalSalesForCurrentCompany()
        );
        model.addAttribute(
                "lowStockProducts",
                inventoryService.getLowStockProducts()
        );
        model.addAttribute(
                "transactions",
                inventoryService.getAllTransactions()
        );
        model.addAttribute(
                "invoices",
                billingService.getInvoicesForCurrentCompany()
        );

        return "reports";
    }
}
