package com.van.monitoring_service.streamer.consumer;

import com.van.monitoring_service.api.service.DashboardQueryService;
import com.van.monitoring_service.domain.dto.Transaction;
import com.van.monitoring_service.domain.dto.TxnDetailDto;
import com.van.monitoring_service.domain.repository.TransactionRepository;
import com.van.monitoring_service.domain.repository.TxnDetailDao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope; // 추가
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@RefreshScope
public class MetricEventConsumer {

    private final SimpMessagingTemplate messagingTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    private final DashboardQueryService dashboardService;

    // Config Server에서 값 주입 (기본값 100000)
    @Value("${monitoring.criteria.high-amount:100000}")
    private Long highAmountCriteria;


    // 1. 신용카드 거래 데이터 수신
    @KafkaListener(topics = "van.dashboard.itmx.crdt", groupId = "dashboard-consumer")
    public void consumeItmxCrdtData(List<TxnDetailDto> data) {
        log.debug("Kafka 수신 (Crdt): {}건", data.size());
        // 1. 실시간 전송 (WebSocket)
        messagingTemplate.convertAndSend("/topic/van/itmx/crdt", data);
        // 2. 최신 상태 저장 (Redis) - 새로고침/신규 접속자용
        redisTemplate.opsForValue().set("itmx_crdt::latest", data);
    }

    // 2. 포인트 거래 데이터 수신
    @KafkaListener(topics = "van.dashboard.itmx.pont", groupId = "dashboard-consumer")
    public void consumeItmxPontData(List<TxnDetailDto> data) {
        messagingTemplate.convertAndSend("/topic/van/itmx/pont", data);
        redisTemplate.opsForValue().set("itmx_pont::latest", data);
    }

    // 3. 현금영수증 거래 데이터 수신
    @KafkaListener(topics = "van.dashboard.itmx.cash", groupId = "dashboard-consumer")
    public void consumeItmxCashData(List<TxnDetailDto> data) {
        messagingTemplate.convertAndSend("/topic/van/itmx/cash", data);
        redisTemplate.opsForValue().set("itmx_cash::latest", data);
    }

    // 4. 신용카드 응답코드별 집계 수신
    @KafkaListener(topics = "van.dashboard.itmx.crdtResp", groupId = "dashboard-consumer")
    public void consumeItmxCrdtRespData(List<TxnDetailDto> data) {
        messagingTemplate.convertAndSend("/topic/van/itmx/crdtResp", data);
        redisTemplate.opsForValue().set("itmx_crdt_resp::latest", data);
    }

    // 5. 포인트 응답코드별 집계 수신
    @KafkaListener(topics = "van.dashboard.itmx.pontResp", groupId = "dashboard-consumer")
    public void consumeItmxPontRespData(List<TxnDetailDto> data) {
        messagingTemplate.convertAndSend("/topic/van/itmx/pontResp", data);
        redisTemplate.opsForValue().set("itmx_pont_resp::latest", data);
    }

    // 6. 현금영수증 응답코드별 집계 수신
    @KafkaListener(topics = "van.dashboard.itmx.cashResp", groupId = "dashboard-consumer")
    public void consumeItmxCashRespData(List<TxnDetailDto> data) {
        messagingTemplate.convertAndSend("/topic/van/itmx/cashResp", data);
        redisTemplate.opsForValue().set("itmx_cash_resp::latest", data);
    }

}