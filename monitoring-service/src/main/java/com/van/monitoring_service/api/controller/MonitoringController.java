package com.van.monitoring_service.api.controller;

import com.van.monitoring_service.api.service.DashboardQueryService;
import com.van.monitoring_service.domain.dto.Transaction;
import com.van.monitoring_service.domain.dto.TxnDetailDto;
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

    private final DashboardQueryService dashboardQueryService;

    @GetMapping("/itmx/crdt")
    public List<TxnDetailDto> getItmxCrdt() {
        return dashboardQueryService.getItmxCrdtData();
    }

    @GetMapping("/itmx/pont")
    public List<TxnDetailDto> getItmxPont() {
        return dashboardQueryService.getItmxPontData();
    }

    @GetMapping("/itmx/cash")
    public List<TxnDetailDto> getItmxCash() {
        return dashboardQueryService.getItmxCashData();
    }

    @GetMapping("/itmx/crdtResp")
    public List<TxnDetailDto> getItmxCrdtResp() {
        return dashboardQueryService.getItmxCrdtRespData();
    }

    @GetMapping("/itmx/pontResp")
    public List<TxnDetailDto> getItmxPontResp() {
        return dashboardQueryService.getItmxPontRespData();
    }

    @GetMapping("/itmx/cashResp")
    public List<TxnDetailDto> getItmxCashResp() {
        return dashboardQueryService.getItmxCashRespData();
    }
}