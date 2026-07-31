package com.example.inventory_management.repository;

import com.example.inventory_management.model.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository
        extends JpaRepository<Product, Integer> {

    List<Product> findAllByOrderByNameAsc();

    List<Product>
    findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(
            String name,
            String category
    );

    @Query("""
            SELECT p
            FROM Product p
            WHERE p.quantity <= p.lowStockLevel
            ORDER BY p.quantity ASC
            """)
    List<Product> findLowStockProducts();
}