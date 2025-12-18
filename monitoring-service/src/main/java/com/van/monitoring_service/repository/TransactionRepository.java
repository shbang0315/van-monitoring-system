package com.van.monitoring_service.repository;

import com.van.monitoring_service.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {  
    @Query(value = "SELECT * FROM transaction_log ORDER BY transaction_time DESC LIMIT 10", nativeQuery = true)
    List<Transaction> findRecentTransactions();
}