package com.van.monitoring_service.service;

import com.van.monitoring_service.domain.TxnDetailDto;
import com.van.monitoring_service.repository.TxnDetailDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final TxnDetailDao txnDetailDao;

    // [캐싱 전략]
    // DataPushService가 10초마다 호출하므로, TTL(RedisConfig)을 5~9초로 설정하면
    // Scheduler가 돌 때마다 DB를 갱신하고 Redis에 저장합니다.
    // 사용자가 새로고침(Controller 호출)할 때는 Redis에서 즉시 가져갑니다.

    @Cacheable(value = "itmx_crdt", key = "'latest'")
    public List<TxnDetailDto> getItmxCrdtData() {
        return txnDetailDao.findItmxCrdtTransaction();
    }

    @Cacheable(value = "itmx_pont", key = "'latest'")
    public List<TxnDetailDto> getItmxPontData() {
        return txnDetailDao.findItmxPontTransaction();
    }

    @Cacheable(value = "itmx_cash", key = "'latest'")
    public List<TxnDetailDto> getItmxCashData() {
        return txnDetailDao.findItmxCashTransaction();
    }

    @Cacheable(value = "itmx_crdt_resp", key = "'latest'")
    public List<TxnDetailDto> getItmxCrdtRespData() {
        return txnDetailDao.findItmxCrdtRespTransaction();
    }

    @Cacheable(value = "itmx_pont_resp", key = "'latest'")
    public List<TxnDetailDto> getItmxPontRespData() {
        return txnDetailDao.findItmxPontRespTransaction();
    }

    @Cacheable(value = "itmx_cash_resp", key = "'latest'")
    public List<TxnDetailDto> getItmxCashRespData() {
        return txnDetailDao.findItmxCashRespTransaction();
    }
}