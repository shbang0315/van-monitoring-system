package com.van.monitoring_service.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TxnDetailDto {
    private LocalDateTime lastTime;       
    private String instCode;
    private String count;
    private String rejectRate;
    private String trxRespCd;
}