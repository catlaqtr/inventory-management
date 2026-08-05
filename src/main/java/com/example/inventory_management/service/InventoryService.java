package com.example.inventory_management.service;

import com.example.inventory_management.model.Company;
import com.example.inventory_management.model.Product;
import com.example.inventory_management.model.StockTransaction;
import com.example.inventory_management.repository.CompanyRepository;
import com.example.inventory_management.repository.ProductRepository;
import com.example.inventory_management.repository.StockTransactionRepository;
import com.example.inventory_management.security.CompanyContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventoryService {

    private final ProductRepository productRepository;
    private final StockTransactionRepository stockTransactionRepository;
    private final CompanyRepository companyRepository;

    public InventoryService(
            ProductRepository productRepository,
            StockTransactionRepository stockTransactionRepository,
            CompanyRepository companyRepository
    ) {
        this.productRepository = productRepository;
        this.stockTransactionRepository = stockTransactionRepository;
        this.companyRepository = companyRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findByCompanyIdOrderByNameAsc(currentCompanyId());
    }

    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts();
        }

        String searchText = keyword.trim();
        Long companyId = currentCompanyId();

        return productRepository
                .findByCompanyIdAndNameContainingIgnoreCaseOrCompanyIdAndCategoryContainingIgnoreCase(
                        companyId,
                        searchText,
                        companyId,
                        searchText
                );
    }

    public Product getProductById(Integer id) {
        return productRepository
                .findByIdAndCompanyId(id, currentCompanyId())
                .orElseThrow(() -> new RuntimeException("Product not found."));
    }

    public long countProducts() {
        return productRepository.countByCompanyId(currentCompanyId());
    }

    @Transactional
    public Product addProduct(Product product) {
        validateProduct(product);

        Company company = companyRepository.findById(currentCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found."));
        product.setCompany(company);

        Product savedProduct = productRepository.save(product);

        if (savedProduct.getQuantity() > 0) {
            StockTransaction transaction = new StockTransaction();
            transaction.setProduct(savedProduct);
            transaction.setType("STOCK_IN");
            transaction.setQuantity(savedProduct.getQuantity());
            transaction.setNote("Initial stock");
            transaction.setTransactionDate(LocalDateTime.now());
            stockTransactionRepository.save(transaction);
        }

        return savedProduct;
    }

    @Transactional
    public Product updateProduct(Integer id, Product updatedProduct) {
        validateProduct(updatedProduct);

        Product existingProduct = getProductById(id);

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setCategory(updatedProduct.getCategory());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setLowStockLevel(updatedProduct.getLowStockLevel());
        existingProduct.setUnitPrice(updatedProduct.getUnitPrice());

        return productRepository.save(existingProduct);
    }

    @Transactional
    public void deleteProduct(Integer id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }

    public List<Product> getLowStockProducts() {
        return productRepository.findLowStockProducts(currentCompanyId());
    }

    public List<StockTransaction> getAllTransactions() {
        return stockTransactionRepository
                .findByProductCompanyIdOrderByTransactionDateDesc(currentCompanyId());
    }

    @Transactional
    public void recordStockTransaction(
            Integer productId,
            String type,
            int quantity,
            String note
    ) {
        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than zero.");
        }

        if (!type.equals("STOCK_IN") && !type.equals("STOCK_OUT")) {
            throw new RuntimeException("Invalid transaction type.");
        }

        Product product = getProductById(productId);

        if (type.equals("STOCK_IN")) {
            product.setQuantity(product.getQuantity() + quantity);
        } else {
            if (product.getQuantity() < quantity) {
                throw new RuntimeException("Not enough stock available.");
            }
            product.setQuantity(product.getQuantity() - quantity);
        }

        productRepository.save(product);

        StockTransaction transaction = new StockTransaction();
        transaction.setProduct(product);
        transaction.setType(type);
        transaction.setQuantity(quantity);
        transaction.setNote(note);
        transaction.setTransactionDate(LocalDateTime.now());
        stockTransactionRepository.save(transaction);
    }

    private Long currentCompanyId() {
        return CompanyContext.requireCompanyId();
    }

    private void validateProduct(Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new RuntimeException("Product name is required.");
        }

        if (product.getCategory() == null || product.getCategory().trim().isEmpty()) {
            throw new RuntimeException("Category is required.");
        }

        if (product.getQuantity() < 0) {
            throw new RuntimeException("Quantity cannot be negative.");
        }

        if (product.getLowStockLevel() < 0) {
            throw new RuntimeException("Low-stock level cannot be negative.");
        }

        if (product.getUnitPrice() == null || product.getUnitPrice().signum() < 0) {
            throw new RuntimeException("Unit price cannot be negative.");
        }
    }
}
