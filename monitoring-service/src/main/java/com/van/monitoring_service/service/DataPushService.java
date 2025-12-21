package com.van.monitoring_service.service;

import com.van.monitoring_service.domain.Transaction;
import com.van.monitoring_service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope; // 추가
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@RefreshScope // <--- 설정값이 바뀌면 이 클래스를 새로고침 하겠다는 뜻!
public class DataPushService {

    private final TransactionRepository transactionRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // Config Server에서 값 주입 (기본값 100000)
    @Value("${monitoring.criteria.high-amount:100000}")
    private Long highAmountCriteria;

    @Scheduled(fixedRate = 3000)
    public void pushRealTimeData() {
        
        // 1. 전체
        messagingTemplate.convertAndSend("/topic/van/all", transactionRepository.findAllLogs());

        // 2. 성공
        messagingTemplate.convertAndSend("/topic/van/success", transactionRepository.findSuccessLogs());
        
        // 3. 실패
        messagingTemplate.convertAndSend("/topic/van/fail", transactionRepository.findFailLogs());
        
        // 4. 취소
        messagingTemplate.convertAndSend("/topic/van/cancel", transactionRepository.findCancelLogs());
        
        // 5. 고액
        messagingTemplate.convertAndSend("/topic/van/high", transactionRepository.findHighAmountLogs());
        
        // 6. 강남
        messagingTemplate.convertAndSend("/topic/van/gangnam", transactionRepository.findGangnamLogs());
        
        log.info("📡 Pushed 6 distinct datasets via WebSocket");
        
        // [로직 추가] 설정된 기준금액보다 크면 Log를 찍거나 상태를 변경
        // for (Transaction t : transactions) {
        //     if (t.getAmount() >= highAmountCriteria) {
        //         log.warn("🚨 고액 결제 감지! (기준: {}원, 결제액: {}원)", highAmountCriteria, t.getAmount());
        //         // 필요하다면 여기서 t.setStatus("HIGH_RISK"); 등으로 바꿔서 보낼 수도 있음
        //     }
        // }

        // messagingTemplate.convertAndSend("/topic/transactions", transactions);
    }
}