package com.example.inventory_management.service;

import com.example.inventory_management.dto.InvoiceForm;
import com.example.inventory_management.dto.InvoiceLineForm;
import com.example.inventory_management.model.*;
import com.example.inventory_management.repository.CompanyRepository;
import com.example.inventory_management.repository.InvoiceRepository;
import com.example.inventory_management.repository.ProductRepository;
import com.example.inventory_management.repository.StockTransactionRepository;
import com.example.inventory_management.security.CompanyContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BillingService {

    private final InvoiceRepository invoiceRepository;
    private final ProductRepository productRepository;
    private final CompanyRepository companyRepository;
    private final StockTransactionRepository stockTransactionRepository;

    public BillingService(
            InvoiceRepository invoiceRepository,
            ProductRepository productRepository,
            CompanyRepository companyRepository,
            StockTransactionRepository stockTransactionRepository
    ) {
        this.invoiceRepository = invoiceRepository;
        this.productRepository = productRepository;
        this.companyRepository = companyRepository;
        this.stockTransactionRepository = stockTransactionRepository;
    }

    public List<Invoice> getInvoicesForCurrentCompany() {
        return invoiceRepository.findByCompanyIdOrderByInvoiceDateDesc(
                CompanyContext.requireCompanyId()
        );
    }

    public Invoice getInvoiceById(Long id) {
        return invoiceRepository
                .findByIdAndCompanyId(id, CompanyContext.requireCompanyId())
                .orElseThrow(() -> new RuntimeException("Invoice not found."));
    }

    public BigDecimal getTotalSalesForCurrentCompany() {
        BigDecimal total = invoiceRepository.sumTotalAmountByCompanyId(
                CompanyContext.requireCompanyId()
        );
        return total == null ? BigDecimal.ZERO : total;
    }

    public BigDecimal getTotalTaxForCurrentCompany() {
        BigDecimal total = invoiceRepository.sumTaxAmountByCompanyId(
                CompanyContext.requireCompanyId()
        );
        return total == null ? BigDecimal.ZERO : total;
    }

    public BigDecimal getTotalSubtotalForCurrentCompany() {
        BigDecimal total = invoiceRepository.sumSubtotalByCompanyId(
                CompanyContext.requireCompanyId()
        );
        return total == null ? BigDecimal.ZERO : total;
    }

    public long countInvoicesForCurrentCompany() {
        return invoiceRepository.countByCompanyId(CompanyContext.requireCompanyId());
    }

    @Transactional
    public Invoice createInvoice(InvoiceForm form) {
        if (form.getCustomerName() == null || form.getCustomerName().isBlank()) {
            throw new RuntimeException("Full name is required.");
        }

        if (form.getLines() == null || form.getLines().isEmpty()) {
            throw new RuntimeException("Add at least one invoice item.");
        }

        ProvincialTax tax = ProvincialTax.fromName(form.getTaxCode());

        Long companyId = CompanyContext.requireCompanyId();
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found."));

        Map<Integer, Integer> quantityByProduct = new HashMap<>();
        for (InvoiceLineForm line : form.getLines()) {
            if (line == null || line.getProductId() == null) {
                continue;
            }
            if (line.getQuantity() == null || line.getQuantity() <= 0) {
                throw new RuntimeException("Each item quantity must be greater than zero.");
            }
            quantityByProduct.merge(line.getProductId(), line.getQuantity(), Integer::sum);
        }

        if (quantityByProduct.isEmpty()) {
            throw new RuntimeException("Add at least one invoice item.");
        }

        Invoice invoice = new Invoice();
        invoice.setCompany(company);
        invoice.setCustomerName(form.getCustomerName().trim());
        invoice.setCustomerMobile(
                form.getCustomerMobile() == null || form.getCustomerMobile().isBlank()
                        ? null
                        : form.getCustomerMobile().trim()
        );
        invoice.setNote(form.getNote() == null || form.getNote().isBlank()
                ? null
                : form.getNote().trim());
        invoice.setInvoiceDate(LocalDateTime.now());
        invoice.setInvoiceNumber(generateInvoiceNumber(companyId));
        invoice.setTaxCode(tax.name());
        invoice.setTaxRate(tax.getRatePercent());

        BigDecimal subtotal = BigDecimal.ZERO;

        for (Map.Entry<Integer, Integer> entry : quantityByProduct.entrySet()) {
            Product product = productRepository
                    .findByIdAndCompanyId(entry.getKey(), companyId)
                    .orElseThrow(() -> new RuntimeException("Product not found."));

            int qty = entry.getValue();
            if (product.getQuantity() < qty) {
                throw new RuntimeException(
                        "Not enough stock for " + product.getName()
                                + ". Available: " + product.getQuantity()
                );
            }

            BigDecimal unitPrice = product.getUnitPrice();
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(qty));

            InvoiceItem item = new InvoiceItem();
            item.setProduct(product);
            item.setQuantity(qty);
            item.setUnitPrice(unitPrice);
            item.setLineTotal(lineTotal);
            invoice.addItem(item);

            product.setQuantity(product.getQuantity() - qty);
            productRepository.save(product);

            StockTransaction transaction = new StockTransaction();
            transaction.setProduct(product);
            transaction.setType("STOCK_OUT");
            transaction.setQuantity(qty);
            transaction.setNote("Invoice " + invoice.getInvoiceNumber());
            transaction.setTransactionDate(LocalDateTime.now());
            stockTransactionRepository.save(transaction);

            subtotal = subtotal.add(lineTotal);
        }

        BigDecimal taxAmount = subtotal
                .multiply(tax.getRatePercent())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(taxAmount);

        invoice.setSubtotalAmount(subtotal.setScale(2, RoundingMode.HALF_UP));
        invoice.setTaxAmount(taxAmount);
        invoice.setTotalAmount(total);

        return invoiceRepository.save(invoice);
    }

    private String generateInvoiceNumber(Long companyId) {
        long count = invoiceRepository.countByCompanyId(companyId) + 1;
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return String.format("INV-%s-%04d", datePart, count);
    }
}
