package com.van.monitoring_service.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEvent {
    private String txId;       // 고유 ID
    private String instCode;   // 기관코드
    private String type;       // CRDT(신용), PONT(포인트), CASH(현금)
    private String respCode;   // 응답코드 (0000: 성공, 그외 실패)
    private Long amount;       // 금액
    private LocalDateTime txTime;
}