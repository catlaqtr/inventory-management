package com.example.inventory_management.repository;

import com.example.inventory_management.model.StockTransaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockTransactionRepository
        extends JpaRepository<StockTransaction, Integer> {

    List<StockTransaction>
    findAllByOrderByTransactionDateDesc();
}