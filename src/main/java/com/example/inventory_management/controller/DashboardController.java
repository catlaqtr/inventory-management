package com.example.inventory_management.controller;

import com.example.inventory_management.dao.InventoryReportDao;
import com.example.inventory_management.security.CompanyContext;
import com.example.inventory_management.security.CompanyUserDetails;
import com.example.inventory_management.service.BillingService;
import com.example.inventory_management.service.InventoryService;
import com.example.inventory_management.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final InventoryService inventoryService;
    private final UserService userService;
    private final BillingService billingService;
    private final InventoryReportDao inventoryReportDao;
    private final ObjectMapper objectMapper;

    public DashboardController(
            InventoryService inventoryService,
            UserService userService,
            BillingService billingService,
            InventoryReportDao inventoryReportDao,
            ObjectMapper objectMapper
    ) {
        this.inventoryService = inventoryService;
        this.userService = userService;
        this.billingService = billingService;
        this.inventoryReportDao = inventoryReportDao;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model, Authentication authentication)
            throws JsonProcessingException {

        CompanyUserDetails user = (CompanyUserDetails) authentication.getPrincipal();
        Long companyId = CompanyContext.requireCompanyId();

        String roleLabel = user.getRole() != null
                ? user.getRole().replace("ROLE_", "")
                : "";

        model.addAttribute("username", user.getUsername());
        model.addAttribute("role", roleLabel);
        model.addAttribute("companyName", user.getCompanyName());

        model.addAttribute("totalStaff", userService.countUsersForCurrentCompany());
        model.addAttribute("totalSales", billingService.getTotalSalesForCurrentCompany());
        model.addAttribute("totalTax", billingService.getTotalTaxForCurrentCompany());
        model.addAttribute("totalProducts", inventoryService.countProducts());
        model.addAttribute("totalStock", inventoryReportDao.getTotalQuantity(companyId));
        model.addAttribute("lowStockCount", inventoryReportDao.getLowStockCount(companyId));
        model.addAttribute("inventoryValue", inventoryReportDao.getTotalInventoryValue(companyId));
        model.addAttribute("totalInvoices", billingService.countInvoicesForCurrentCompany());

        model.addAttribute(
                "monthlySalesJson",
                objectMapper.writeValueAsString(
                        inventoryReportDao.getMonthlySales(companyId)
                )
        );

        return "dashboard";
    }
}
