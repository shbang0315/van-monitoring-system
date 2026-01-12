package com.van.monitoring_service.collector.scheduler;

import com.van.monitoring_service.domain.dto.TxnDetailDto;
import com.van.monitoring_service.domain.repository.TxnDetailDao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetricCollectorScheduler {

    private final TxnDetailDao txnDetailDao;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    /**
     * 신용카드 거래 집계 -> Kafka 전송
     */
    @Scheduled(fixedRateString = "10000") // 10초마다 실행
    public void produceItmxCrdtData() {
        try {
            // 1. DB 조회 (가장 무거운 작업)
            List<TxnDetailDto> data = txnDetailDao.findItmxCrdtTransaction();
            
            // 2. Kafka Topic으로 전송
            // Topic 이름 규칙: van.dashboard.{업무}.{유형}
            kafkaTemplate.send("van.dashboard.itmx.crdt", data);
            
            log.info("📤 [Producer] 신용카드 데이터 전송 완료 ({}건)", data.size());
        } catch (Exception e) {
            log.error("❌ 신용카드 데이터 생성 중 오류 발생", e);
        }
    }

    /**
     * 포인트 거래 집계 -> Kafka 전송
     */
    @Scheduled(fixedRateString = "10000")
    public void produceItmxPontData() {
        try {
            List<TxnDetailDto> data = txnDetailDao.findItmxPontTransaction();
            kafkaTemplate.send("van.dashboard.itmx.pont", data);
            log.info("📤 [Producer] 포인트 데이터 전송 완료 ({}건)", data.size());
        } catch (Exception e) {
            log.error("❌ 포인트 데이터 생성 중 오류 발생", e);
        }
    }

    /**
     * 현금영수증 거래 집계 -> Kafka 전송
     */
    @Scheduled(fixedRateString = "10000")
    public void produceItmxCashData() {
        try {
            List<TxnDetailDto> data = txnDetailDao.findItmxCashTransaction();
            kafkaTemplate.send("van.dashboard.itmx.cash", data);
            log.info("📤 [Producer] 현금영수증 데이터 전송 완료 ({}건)", data.size());
        } catch (Exception e) {
            log.error("❌ 현금영수증 데이터 생성 중 오류 발생", e);
        }
    }

    @Scheduled(fixedRateString = "10000")
    public void produceItmxCrdtRespData() {
        List<TxnDetailDto> data = txnDetailDao.findItmxCrdtRespTransaction();
        kafkaTemplate.send("van.dashboard.itmx.crdtResp", data);
    }

    @Scheduled(fixedRateString = "10000")
    public void produceItmxPontRespData() {
        List<TxnDetailDto> data = txnDetailDao.findItmxPontRespTransaction();
        kafkaTemplate.send("van.dashboard.itmx.pontResp", data);
    }

    @Scheduled(fixedRateString = "10000")
    public void produceItmxCashRespData() {
        List<TxnDetailDto> data = txnDetailDao.findItmxCashRespTransaction();
        kafkaTemplate.send("van.dashboard.itmx.cashResp", data);
    }
}