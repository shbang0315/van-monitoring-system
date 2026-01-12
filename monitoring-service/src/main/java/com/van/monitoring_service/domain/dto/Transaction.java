package com.van.monitoring_service.domain.dto;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "store_id")
    private String storeId;

    @Column(name = "store_name")
    private String storeName;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "status")
    private String status;

    @Column(name = "error_code")
    private String errorCode;

    @Column(name = "transaction_time")
    private LocalDateTime transactionTime;
}