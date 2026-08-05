package com.example.inventory_management.controller;

import com.example.inventory_management.dto.InvoiceForm;
import com.example.inventory_management.dto.InvoiceLineForm;
import com.example.inventory_management.model.ProvincialTax;
import com.example.inventory_management.service.BillingService;
import com.example.inventory_management.service.InventoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/billing")
public class BillingController {

    private final BillingService billingService;
    private final InventoryService inventoryService;

    public BillingController(
            BillingService billingService,
            InventoryService inventoryService
    ) {
        this.billingService = billingService;
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public String listInvoices(Model model) {
        model.addAttribute("invoices", billingService.getInvoicesForCurrentCompany());
        model.addAttribute("totalSales", billingService.getTotalSalesForCurrentCompany());
        return "invoices";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        InvoiceForm form = new InvoiceForm();
        form.getLines().clear();
        form.getLines().add(new InvoiceLineForm());

        populateFormModel(model, form);
        return "invoice-form";
    }

    @PostMapping("/save")
    public String saveInvoice(
            @ModelAttribute("invoiceForm") InvoiceForm invoiceForm,
            Model model
    ) {
        try {
            var invoice = billingService.createInvoice(invoiceForm);
            return "redirect:/billing/" + invoice.getId();
        } catch (RuntimeException exception) {
            model.addAttribute("error", exception.getMessage());
            if (invoiceForm.getLines() == null || invoiceForm.getLines().isEmpty()) {
                invoiceForm.getLines().add(new InvoiceLineForm());
            }
            populateFormModel(model, invoiceForm);
            return "invoice-form";
        }
    }

    @GetMapping("/{id}")
    public String viewInvoice(@PathVariable Long id, Model model) {
        model.addAttribute("invoice", billingService.getInvoiceById(id));
        return "invoice-detail";
    }

    private void populateFormModel(Model model, InvoiceForm form) {
        model.addAttribute("invoiceForm", form);
        model.addAttribute("products", inventoryService.getAllProducts());
        model.addAttribute("taxOptions", ProvincialTax.all());
    }
}
