package com.van.monitoring_service.repository;

import com.van.monitoring_service.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    // 1. 전체 거래 (기존)
    @Query(value = "SELECT * FROM transaction_log ORDER BY transaction_time DESC LIMIT 30", nativeQuery = true)
    List<Transaction> findAllLogs();

    // 2. 승인 성공 (SUCCESS)
    @Query(value = "SELECT * FROM transaction_log WHERE status = 'SUCCESS' ORDER BY transaction_time DESC LIMIT 30", nativeQuery = true)
    List<Transaction> findSuccessLogs();

    // 3. 오류/실패 (FAIL)
    @Query(value = "SELECT * FROM transaction_log WHERE status = 'FAIL' ORDER BY transaction_time DESC LIMIT 30", nativeQuery = true)
    List<Transaction> findFailLogs();

    // 4. 취소 거래 (CANCEL)
    @Query(value = "SELECT * FROM transaction_log WHERE status = 'CANCEL' ORDER BY transaction_time DESC LIMIT 30", nativeQuery = true)
    List<Transaction> findCancelLogs();

    // 5. 고액 결제 (10만원 이상) -> 조건은 나중에 Config로 뺄 수도 있음
    @Query(value = "SELECT * FROM transaction_log WHERE amount >= 100000 ORDER BY transaction_time DESC LIMIT 30", nativeQuery = true)
    List<Transaction> findHighAmountLogs();

    // 6. VIP 점포 (강남본점)
    @Query(value = "SELECT * FROM transaction_log WHERE store_name LIKE '%강남%' ORDER BY transaction_time DESC LIMIT 30", nativeQuery = true)
    List<Transaction> findGangnamLogs();
}