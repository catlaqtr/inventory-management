package com.example.inventory_management.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public class InventoryReportDao {

    private final JdbcTemplate jdbcTemplate;

    public InventoryReportDao(
            JdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long getTotalProducts() {
        Long result = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM products",
                Long.class
        );

        return result == null ? 0L : result;
    }

    public Long getTotalQuantity() {
        Long result = jdbcTemplate.queryForObject(
                """
                SELECT COALESCE(SUM(quantity), 0)
                FROM products
                """,
                Long.class
        );

        return result == null ? 0L : result;
    }

    public BigDecimal getTotalInventoryValue() {
        BigDecimal result =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COALESCE(
                            SUM(quantity * unit_price),
                            0
                        )
                        FROM products
                        """,
                        BigDecimal.class
                );

        return result == null
                ? BigDecimal.ZERO
                : result;
    }
}