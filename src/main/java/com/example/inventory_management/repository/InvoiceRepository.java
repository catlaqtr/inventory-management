package com.example.inventory_management.repository;

import com.example.inventory_management.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByCompanyIdOrderByInvoiceDateDesc(Long companyId);

    @Query("""
            SELECT DISTINCT i
            FROM Invoice i
            LEFT JOIN FETCH i.items items
            LEFT JOIN FETCH items.product
            WHERE i.id = :id
              AND i.company.id = :companyId
            """)
    Optional<Invoice> findByIdAndCompanyId(
            @Param("id") Long id,
            @Param("companyId") Long companyId
    );

    long countByCompanyId(Long companyId);

    @Query("""
            SELECT COALESCE(SUM(i.totalAmount), 0)
            FROM Invoice i
            WHERE i.company.id = :companyId
            """)
    BigDecimal sumTotalAmountByCompanyId(@Param("companyId") Long companyId);

    @Query("""
            SELECT COALESCE(SUM(i.taxAmount), 0)
            FROM Invoice i
            WHERE i.company.id = :companyId
            """)
    BigDecimal sumTaxAmountByCompanyId(@Param("companyId") Long companyId);

    @Query("""
            SELECT COALESCE(SUM(i.subtotalAmount), 0)
            FROM Invoice i
            WHERE i.company.id = :companyId
            """)
    BigDecimal sumSubtotalByCompanyId(@Param("companyId") Long companyId);
}
