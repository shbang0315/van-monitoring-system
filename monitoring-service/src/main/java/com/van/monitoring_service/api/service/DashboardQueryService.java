package com.van.monitoring_service.api.service;

import com.van.monitoring_service.domain.dto.TxnDetailDto;
import com.van.monitoring_service.domain.repository.TxnDetailDao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardQueryService {

    private final RedisTemplate<String, Object> redisTemplate;

    public List<TxnDetailDto> getItmxCrdtData() {
        return getRedisData("itmx_crdt::latest");
    }

    public List<TxnDetailDto> getItmxPontData() {
        return getRedisData("itmx_pont::latest");
    }

    public List<TxnDetailDto> getItmxCashData() {
        return getRedisData("itmx_cash::latest");
    }

    public List<TxnDetailDto> getItmxCrdtRespData() {
        return getRedisData("itmx_crdt_resp::latest");
    }

    public List<TxnDetailDto> getItmxPontRespData() {
        return getRedisData("itmx_pont_resp::latest");
    }

    public List<TxnDetailDto> getItmxCashRespData() {
        return getRedisData("itmx_cash_resp::latest");
    }

    @SuppressWarnings("unchecked")
    private List<TxnDetailDto> getRedisData(String key) {
        Object data = redisTemplate.opsForValue().get(key);
        if (data == null) {
            return Collections.emptyList(); // 데이터가 아직 안 만들어졌으면 빈 리스트
        }
        return (List<TxnDetailDto>) data;
    }
}