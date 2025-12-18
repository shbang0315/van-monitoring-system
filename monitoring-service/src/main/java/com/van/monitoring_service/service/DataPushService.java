package com.van.monitoring_service.service;

import com.van.monitoring_service.domain.Transaction;
import com.van.monitoring_service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataPushService {

    private final TransactionRepository transactionRepository;
    private final SimpMessagingTemplate messagingTemplate; // 메시지 발송 도구

    // 3초(3000ms)마다 실행
    @Scheduled(fixedRate = 3000)
    public void pushRealTimeData() {
        try {
            // 1. DB에서 최근 거래내역 10건 조회
            List<Transaction> transactions = transactionRepository.findRecentTransactions();
            
            // 2. 데이터가 있다면 WebSocket 구독자들에게 전송
            if (transactions != null && !transactions.isEmpty()) {
                // "/topic/transactions" 채널을 구독 중인 프론트엔드에게 데이터 발송
                messagingTemplate.convertAndSend("/topic/transactions", transactions);
                
                // 로그 확인용
                log.info("📡 Real-time data pushed via WebSocket: {} items", transactions.size());
            }
        } catch (Exception e) {
            log.error("❌ Error pushing data: ", e);
        }
    }
}