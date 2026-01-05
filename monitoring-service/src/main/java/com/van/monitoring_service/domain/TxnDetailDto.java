package com.van.monitoring_service.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TxnDetailDto {
    private LocalDateTime time;       
    private String tx_code;
    private String login_name;
}