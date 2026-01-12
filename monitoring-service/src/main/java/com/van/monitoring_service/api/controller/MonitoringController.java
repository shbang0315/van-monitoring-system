package com.van.monitoring_service.api.controller;

import com.van.monitoring_service.domain.dto.Transaction;
import com.van.monitoring_service.domain.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/monitoring")
@RequiredArgsConstructor
public class MonitoringController {

    private final TransactionRepository transactionRepository;

    @GetMapping("/recent")
    public List<Transaction> getRecentTransactions() {
        return transactionRepository.findAllLogs();
    }
}