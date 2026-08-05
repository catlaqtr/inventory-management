package com.example.inventory_management.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InventoryReportDao {

    private final JdbcTemplate jdbcTemplate;

    public InventoryReportDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long getTotalProducts(Long companyId) {
        Long result = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM products WHERE company_id = ?",
                Long.class,
                companyId
        );

        return result == null ? 0L : result;
    }

    public Long getTotalQuantity(Long companyId) {
        Long result = jdbcTemplate.queryForObject(
                """
                SELECT COALESCE(SUM(quantity), 0)
                FROM products
                WHERE company_id = ?
                """,
                Long.class,
                companyId
        );

        return result == null ? 0L : result;
    }

    public Long getLowStockCount(Long companyId) {
        Long result = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM products
                WHERE company_id = ?
                  AND quantity <= low_stock_level
                """,
                Long.class,
                companyId
        );

        return result == null ? 0L : result;
    }

    public BigDecimal getTotalInventoryValue(Long companyId) {
        BigDecimal result = jdbcTemplate.queryForObject(
                """
                SELECT COALESCE(SUM(quantity * unit_price), 0)
                FROM products
                WHERE company_id = ?
                """,
                BigDecimal.class,
                companyId
        );

        return result == null ? BigDecimal.ZERO : result;
    }

    public List<Map<String, Object>> getMonthlySales(Long companyId) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                """
                SELECT YEAR(invoice_date) AS sale_year,
                       MONTH(invoice_date) AS sale_month,
                       COALESCE(SUM(total_amount), 0) AS sales_total,
                       COALESCE(SUM(tax_amount), 0) AS tax_total
                FROM invoices
                WHERE company_id = ?
                  AND invoice_date >= DATE_SUB(CURDATE(), INTERVAL 11 MONTH)
                GROUP BY YEAR(invoice_date), MONTH(invoice_date)
                ORDER BY sale_year, sale_month
                """,
                companyId
        );

        Map<String, Map<String, Object>> byKey = new HashMap<>();
        for (Map<String, Object> row : rows) {
            String key = row.get("sale_year") + "-" + row.get("sale_month");
            byKey.put(key, row);
        }

        List<Map<String, Object>> months = new ArrayList<>();
        java.time.LocalDate cursor = java.time.LocalDate.now().withDayOfMonth(1).minusMonths(11);

        for (int i = 0; i < 12; i++) {
            String key = cursor.getYear() + "-" + cursor.getMonthValue();
            Map<String, Object> point = new LinkedHashMap<>();
            point.put("label", cursor.format(java.time.format.DateTimeFormatter.ofPattern("MMM")));
            point.put("year", cursor.getYear());
            point.put("month", cursor.getMonthValue());

            Map<String, Object> existing = byKey.get(key);
            if (existing != null) {
                point.put("sales", existing.get("sales_total"));
                point.put("tax", existing.get("tax_total"));
            } else {
                point.put("sales", BigDecimal.ZERO);
                point.put("tax", BigDecimal.ZERO);
            }

            months.add(point);
            cursor = cursor.plusMonths(1);
        }

        return months;
    }
}
