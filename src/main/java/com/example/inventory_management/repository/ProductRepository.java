package com.example.inventory_management.repository;

import com.example.inventory_management.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    long countByCompanyId(Long companyId);

    List<Product> findByCompanyIdOrderByNameAsc(Long companyId);

    Optional<Product> findByIdAndCompanyId(Integer id, Long companyId);

    List<Product> findByCompanyIdAndNameContainingIgnoreCaseOrCompanyIdAndCategoryContainingIgnoreCase(
            Long companyId1,
            String name,
            Long companyId2,
            String category
    );

    @Query("""
            SELECT p
            FROM Product p
            WHERE p.company.id = :companyId
              AND p.quantity <= p.lowStockLevel
            ORDER BY p.quantity ASC
            """)
    List<Product> findLowStockProducts(@Param("companyId") Long companyId);
}
